import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgxPaginationModule } from 'ngx-pagination';
import { Observable, tap } from 'rxjs';

interface User {
  id: number;
  username: string;
  password?: string;
  role: string;
  createdAt?: string;
  updatedAt?: string;
}

interface UserUpdateDTO {
  username?: string;
  password?: string;
  role?: string;
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, NgxPaginationModule],
  templateUrl: './users.html',
  styleUrl: './users.css'
})
export class UsersComponent implements OnInit {
  private apiUrl = 'http://localhost:8080/admin';
  username = '';
  users: User[] = [];
  filteredUsers: User[] = [];
  newUser: User = { id: 0, username: '', password: '', role: 'USER' };
  selectedUser: User | null = null;
  updateDTO: UserUpdateDTO = {};
  p: number = 1;
  itemsPerPage: number = 10;
  searchTerm: string = '';
  loading = false;
  showModal = false;
  errorMessage = '';
  currentUserId: number | null = null;
  currentUserRole: string | null = null;

  constructor(private auth: AuthService, private http: HttpClient, private router: Router) {}


  ngOnInit(): void {
    this.username = this.auth.getUsername() || '';
    
    this.loadUsers();
    
  }

  loadUsers() {
    this.loading = true;
    this.http.get<User[]>(`${this.apiUrl}/users`).subscribe({
      next: (data) => {
        this.users = data;
        this.filteredUsers = data;
        this.loading = false;

        const currentUser = data.find(user => user.username.toLowerCase() === this.username.toLowerCase());


        if (currentUser) {
          this.username = currentUser.username;
          this.currentUserId = currentUser.id;
          this.currentUserRole = currentUser.role;
          /* console.log(`El ID del usuario actual (${this.username}) es: ${this.currentUserId}`); */
          /* console.log(`El ROL del usuario actual (${this.username}) es: ${this.currentUserRole}`); */
        } else {
          /* console.warn(`No se encontró al usuario ${this.username} en la lista.`); */
        }
      },
      error: (error: HttpErrorResponse) => {
        this.handleError('Error al cargar usuarios', error);
        this.loading = false;
      }
    });
  }

  createUser(isAdmin: boolean = false) {
    if (!this.validateUser(this.newUser)) return;

    const endpoint = isAdmin ? 'registeradmin' : 'register';
    this.loading = true;

    this.http.post<User>(`${this.apiUrl}/${endpoint}`, this.newUser).subscribe({
      next: (user) => {
        this.users.unshift(user);
        this.filteredUsers = [...this.users];
        this.resetNewUser();
        this.loading = false;
        this.showSuccessMessage(`Usuario ${isAdmin ? 'administrador' : ''} creado con éxito`);
      },
      error: (error: HttpErrorResponse) => {
        this.handleError('Error al crear usuario', error);
        this.loading = false;
      }
    });
  }

  selectUser(user: User) {
    this.selectedUser = { ...user };
    this.updateDTO = {
      username: user.username,
      role: user.role
    };
    this.showModal = true;
  }





  updateUser() {
    if (!this.selectedUser || !this.validateUpdate()) return;

    const updateData = this.prepareUpdateData();
    if (Object.keys(updateData).length === 0) {
      this.showModal = false;
      return;
    }
    /* console.log('Usuarios: ', this.users); */
    if (this.users.some(u => u.username === updateData.username && u.id !== this.selectedUser!.id)) {
      alert('El username ya existe. Por favor, elige otro.'); 
      return;
    }

    this.loading = true;
    this.http.patch<User>(`${this.apiUrl}/user/${this.selectedUser.id}`, updateData).subscribe({
    next: (updatedUser) => {

      
      /* console.log(updatedUser.username,updatedUser.role,this.auth.getUserRole(), this.currentUserId!); */
      // 👇 le pasamos el user actualizado y el id original
      this.auth.refreshSessionAfterUpdate(updatedUser, this.currentUserId!);
      /* console.log(updatedUser.username,updatedUser.role,this.auth.getUserRole(), this.currentUserId!); */
      const index = this.users.findIndex(u => u.id === updatedUser.id);
      if (index !== -1) {
        this.users[index] = updatedUser;
        this.filteredUsers = [...this.users];
      }
      
      this.closeModal();
      this.loading = false;
      this.showSuccessMessage('Usuario actualizado con éxito');
      
    
    },
    error: (error: HttpErrorResponse) => {
      this.handleError('Error al actualizar usuario', error);
      this.loading = false;
    }
  });

  }

  deleteUser(id: number) {
    if (!confirm('¿Estás seguro de eliminar este usuario?')) return;

    this.loading = true;
    this.http.delete(`${this.apiUrl}/user/${id}`).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.id !== id);
        this.filteredUsers = [...this.users];
        this.loading = false;
        this.showSuccessMessage('Usuario eliminado con éxito');
      },
      error: (error: HttpErrorResponse) => {
        this.handleError('Error al eliminar usuario', error);
        this.loading = false;
      }
    });
  }

  searchUsers() {
    if (!this.searchTerm.trim()) {
      this.filteredUsers = [...this.users];
      return;
    }

    const searchTerm = this.searchTerm.toLowerCase();
    this.filteredUsers = this.users.filter(user =>
      user.username.toLowerCase().includes(searchTerm) ||
      user.role.toLowerCase().includes(searchTerm) ||
      user.id.toString().includes(searchTerm)
      
    );
  }

  private validateUser(user: User): boolean {
    if (!user.username || user.username.length < 3) {
      this.errorMessage = 'El username debe tener al menos 3 caracteres';
      return false;
    }
    if (!user.password || user.password.length < 6) {
      this.errorMessage = 'La contraseña debe tener al menos 6 caracteres';
      return false;
    }
    return true;
  }

  private validateUpdate(): boolean {
    if (this.updateDTO.username && this.updateDTO.username.length < 3) {
      this.errorMessage = 'El username debe tener al menos 3 caracteres';
      return false;
    }
    if (this.updateDTO.password && this.updateDTO.password.length < 6) {
      this.errorMessage = 'La contraseña debe tener al menos 6 caracteres';
      return false;
    }
    return true;
  }

  private prepareUpdateData(): UserUpdateDTO {
    const updateData: UserUpdateDTO = {};
    if (this.updateDTO.username && this.updateDTO.username !== this.selectedUser?.username) {
      updateData.username = this.updateDTO.username;
    }
    if (this.updateDTO.password) {
      updateData.password = this.updateDTO.password;
    }
    if (this.updateDTO.role && this.updateDTO.role !== this.selectedUser?.role) {
      updateData.role = this.updateDTO.role;
    }
    return updateData;
  }

  private resetNewUser() {
    this.newUser = { id: 0, username: '', password: '', role: 'USER' };
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
    this.selectedUser = null;
    this.updateDTO = {};
    this.errorMessage = '';
  }
}