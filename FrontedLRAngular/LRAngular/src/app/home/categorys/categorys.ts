import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/auth.service';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgxPaginationModule } from 'ngx-pagination';

interface Category {
  id: number;
  name: string;
  type: string;
}

interface NewCategory {
  id: number;
  name: string;
  type: string;
}

interface CategoryUpdateDTO {
  name?: string;
  type?: string;
}

@Component({
  selector: 'app-categorys',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, NgxPaginationModule],
  templateUrl: './categorys.html',
  styleUrls: ['./categorys.css']
})
export class CategoryComponent implements OnInit {
  private apiUrl = 'http://localhost:8080/category';

  username = '';
  categories: Category[] = [];
  filteredCategories: Category[] = [];
  newCategory: NewCategory = { id: 0, name: '', type: '' };
  selectedCategory: Category | null = null;
  updateDTO: CategoryUpdateDTO = {};
  p = 1;
  itemsPerPage = 10;
  searchTerm = '';
  loading = false;
  showModal = false;
  errorMessage = '';

  constructor(private auth: AuthService, private http: HttpClient) {}

  ngOnInit(): void {
    this.username = this.auth.getUsername() || '';
    this.loadCategories();
  }

  loadCategories() {
    this.loading = true;
    this.http.get<Category[]>(`${this.apiUrl}/categorys`).subscribe({
      next: (data) => {
        this.categories = data || [];
        this.filteredCategories = [...this.categories];
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.handleError('Error al cargar categorías', err);
        this.loading = false;
      }
    });
  }

  createCategory() {
    if (!this.validateNewCategory(this.newCategory)) return;
    this.loading = true;
    this.http.post(`${this.apiUrl}/create`, this.newCategory, { responseType: 'text' as 'json' }).subscribe({
      next: () => {
        this.resetNewCategory();
        this.loading = false;
        this.showSuccessMessage('Categoría creada con éxito');
        this.loadCategories();
      },
      error: (err: HttpErrorResponse) => {
        this.handleError('Error al crear categoría', err);
        this.loading = false;
      }
    });
  }

  selectCategory(cat: Category) {
    this.selectedCategory = { ...cat };
    this.updateDTO = { name: cat.name, type: cat.type };
    this.showModal = true;
  }

  updateCategory() {
    if (!this.selectedCategory) return;
    if (!this.validateUpdate()) return;

    const payload: CategoryUpdateDTO = {};
    if (this.updateDTO.name && this.updateDTO.name !== this.selectedCategory.name) payload.name = this.updateDTO.name;
    if (this.updateDTO.type && this.updateDTO.type !== this.selectedCategory.type) payload.type = this.updateDTO.type;
    if (Object.keys(payload).length === 0) {
      this.closeModal();
      return;
    }

    this.loading = true;
    this.http.patch<Category>(`${this.apiUrl}/update/${this.selectedCategory.id}`, payload).subscribe({
      next: (updated) => {
        const idx = this.categories.findIndex(c => c.id === updated.id);
        if (idx !== -1) this.categories[idx] = updated;
        this.filteredCategories = [...this.categories];
        this.closeModal();
        this.loading = false;
        this.showSuccessMessage('Categoría actualizada con éxito');
      },
      error: (err: HttpErrorResponse) => {
        this.handleError('Error al actualizar categoría', err);
        this.loading = false;
      }
    });
  }

  deleteCategory(id: number) {
    if (!confirm('¿Estás seguro de eliminar esta categoría?')) return;
    this.loading = true;
    this.http.delete(`${this.apiUrl}/delete/${id}`, { responseType: 'text' as 'json' }).subscribe({
      next: () => {
        this.categories = this.categories.filter(c => c.id !== id);
        this.filteredCategories = [...this.categories];
        this.loading = false;
        this.showSuccessMessage('Categoría eliminada con éxito');
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 403) {
          this.errorMessage = 'No se puede eliminar: la categoría está ligada a otros registros.';
        } else if (err.status === 404) {
          this.errorMessage = 'Categoría no encontrada';
        } else if (err.error && typeof err.error === 'string') {
          this.errorMessage = err.error;
        } else {
          this.errorMessage = 'Error al eliminar categoría';
        }
        console.error('Error al eliminar categoría', err);
        this.loading = false;
        setTimeout(() => this.errorMessage = '', 6000);
      }
    });
  }

  searchCategories() {
    if (!this.searchTerm.trim()) {
      this.filteredCategories = [...this.categories];
      return;
    }
    const term = this.searchTerm.toLowerCase();
    this.filteredCategories = this.categories.filter(c =>
      (c.name || '').toLowerCase().includes(term) ||
      (c.type || '').toLowerCase().includes(term) ||
      c.id.toString().includes(term)
    );
  }

  private validateNewCategory(cat: NewCategory): boolean {
    if (!cat.name || cat.name.trim().length === 0) {
      this.errorMessage = 'El nombre es requerido';
      return false;
    }
    if (!cat.type || cat.type.trim().length === 0) {
      this.errorMessage = 'El tipo es requerido';
      return false;
    }
    return true;
  }

  private validateUpdate(): boolean {
    if (this.updateDTO.name !== undefined && this.updateDTO.name.trim().length === 0) {
      this.errorMessage = 'El nombre no puede estar vacío';
      return false;
    }
    return true;
  }

  private resetNewCategory() {
    this.newCategory = { id: 0, name: '', type: '' };
    this.errorMessage = '';
  }

  private handleError(message: string, error: HttpErrorResponse) {
    console.error(message, error);
    this.errorMessage = (error?.error && (error.error as any).message) || message;
    setTimeout(() => this.errorMessage = '', 5000);
  }

  private showSuccessMessage(message: string) {
    // Opcional: reemplazar por UI toast si tienes
    alert(message);
  }

  closeModal() {
    this.showModal = false;
    this.selectedCategory = null;
    this.updateDTO = {};
    this.errorMessage = '';
  }
}