import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/auth.service';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgxPaginationModule } from 'ngx-pagination';

interface Account {
  id: number;
  name: string;
  type: string;
  currentBalance: number;
}

interface NewAccount {
  id: number;
  name: string;
  type: string;
  currentBalance: number;
}

interface AccountUpdateDTO {
  name?: string;
  type?: string;
  currentBalance?: number;
}

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, NgxPaginationModule],
  templateUrl: './accounts.html',
  styleUrls: ['./accounts.css']
})
export class AccountComponent implements OnInit {
  private apiUrl = 'http://localhost:8080/account';

  username = '';
  accounts: Account[] = [];
  filteredAccounts: Account[] = [];
  newAccount: NewAccount = { id: 0, name: '', type: '', currentBalance: 0 };
  selectedAccount: Account | null = null;
  updateDTO: AccountUpdateDTO = {};
  p = 1;
  itemsPerPage = 10;
  searchTerm = '';
  loading = false;
  showModal = false;
  errorMessage = '';

  constructor(private auth: AuthService, private http: HttpClient) {}

  ngOnInit(): void {
    this.username = this.auth.getUsername() || '';
    this.loadAccounts();
  }

  loadAccounts() {
    this.loading = true;
    this.http.get<Account[]>(`${this.apiUrl}/accounts`).subscribe({
      next: (data) => {
        this.accounts = data;
        this.filteredAccounts = [...data];
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        this.handleError('Error al cargar cuentas', err);
        this.loading = false;
      }
    });
  }

  createAccount() {
    if (!this.validateNewAccount(this.newAccount)) return;
    this.loading = true;

    this.http.post(`${this.apiUrl}/create`, this.newAccount, { responseType: 'text' }).subscribe({
       next: () => {
         this.resetNewAccount();
         this.loading = false;
         this.showSuccessMessage('Cuenta creada con éxito');
         this.loadAccounts();
       },
       error: (err: HttpErrorResponse) => {
         this.handleError('Error al crear cuenta', err);
         this.loading = false;
       }
     });
  }

  selectAccount(acc: Account) {
    this.selectedAccount = { ...acc };
    this.updateDTO = {
      name: acc.name,
      type: acc.type,
      currentBalance: acc.currentBalance
    };
    this.showModal = true;
  }

  updateAccount() {
    if (!this.selectedAccount) return;
    if (!this.validateUpdate()) return;

    const payload: AccountUpdateDTO = this.prepareUpdateData();
    if (Object.keys(payload).length === 0) {
      this.closeModal();
      return;
    }

    this.loading = true;
    this.http.patch<Account>(`${this.apiUrl}/update/${this.selectedAccount.id}`, payload).subscribe({
      next: (updated) => {
        const idx = this.accounts.findIndex(a => a.id === updated.id);
        if (idx !== -1) this.accounts[idx] = updated;
        this.filteredAccounts = [...this.accounts];
        this.closeModal();
        this.loading = false;
        this.showSuccessMessage('Cuenta actualizada con éxito');
      },
      error: (err: HttpErrorResponse) => {
        this.handleError('Error al actualizar cuenta', err);
        this.loading = false;
      }
    });
  }

  deleteAccount(id: number) {
    if (!confirm('¿Estás seguro de eliminar esta cuenta?')) return;
    this.loading = true;
    this.http.delete(`${this.apiUrl}/delete/${id}`).subscribe({
      next: () => {
        this.accounts = this.accounts.filter(a => a.id !== id);
        this.filteredAccounts = [...this.accounts];
        this.loading = false;
        this.showSuccessMessage('Cuenta eliminada con éxito');
      },
      error: (err: HttpErrorResponse) => {

        if (err.status === 403) {
          this.handleError('No se puede eliminar cuenta ya que tiene movimiento ligados', err);
          this.loading = false;
 
        }else if (err.status === 404) {
          this.handleError('Cuenta no encontrada', err);
          this.loading = false;
        }else{
          this.handleError('Error al eliminar cuenta', err);
          this.loading = false;

        }


      }
    });
  }

  searchAccounts() {
    if (!this.searchTerm.trim()) {
      this.filteredAccounts = [...this.accounts];
      return;
    }
    const term = this.searchTerm.toLowerCase();
    this.filteredAccounts = this.accounts.filter(a =>
      a.name.toLowerCase().includes(term) ||
      a.type.toLowerCase().includes(term) ||
      a.id.toString().includes(term) ||
      a.currentBalance.toString().includes(term)
    );
  }

  private validateNewAccount(acc: NewAccount): boolean {
    if (!acc.name || acc.name.trim().length === 0) {
      this.errorMessage = 'El nombre es requerido';
      return false;
    }
    if (!acc.type || acc.type.trim().length === 0) {
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

  private prepareUpdateData(): AccountUpdateDTO {
    const out: AccountUpdateDTO = {};
    if (this.updateDTO.name && this.updateDTO.name !== this.selectedAccount?.name) out.name = this.updateDTO.name;
    if (this.updateDTO.type && this.updateDTO.type !== this.selectedAccount?.type) out.type = this.updateDTO.type;
    if (this.updateDTO.currentBalance !== undefined && this.updateDTO.currentBalance !== this.selectedAccount?.currentBalance) out.currentBalance = this.updateDTO.currentBalance;
    return out;
  }

  private resetNewAccount() {
    this.newAccount = { id: 0, name: '', type: '', currentBalance: 0 };
    this.errorMessage = '';
  }

  private handleError(message: string, error: HttpErrorResponse) {
    console.error(message, error);
    this.errorMessage = error.error?.message || message;
    setTimeout(() => this.errorMessage = '', 5000);
  }

  private showSuccessMessage(message: string) {
    this.errorMessage = '';
    alert(message);
  }

  closeModal() {
    this.showModal = false;
    this.selectedAccount = null;
    this.updateDTO = {};
    this.errorMessage = '';
  }
}