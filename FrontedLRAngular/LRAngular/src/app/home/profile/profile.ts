import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/auth.service';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
interface User {
  id: number;
  username: string;
  password?: string;
  role: string;
  createdAt?: string;
  updatedAt?: string;
}

interface UserUpdateDTOUSER {
  username?: string;
  password?: string;
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class ProfileComponent implements OnInit {
  private apiUrl = 'http://localhost:8080';
  username = '';
  userProfile?: User;
  loading = false;
  error = '';
  showModal = false;
  updateDTO: UserUpdateDTOUSER = {};
  selectedUser: User | null = null;
  currentUserId: number | null = null;
  currentUserRole= 'ROLE_USER';
  users: User[] = [];

  constructor(
    private auth: AuthService,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    this.username = this.auth.getUsername() || '';
    this.loadUserProfile();
    this.loadUsers();
  }

loadUsers() {
    this.loading = true;
    this.http.get<User[]>(`${this.apiUrl}/users/users`).subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;

      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading users:', error);
        this.loading = false;
      }
    });
  }

  loadUserProfile() {
    if (!this.username) return;

    this.loading = true;
    this.http.get<User>(`${this.apiUrl}/users/username/${this.username}`).subscribe({
      next: (profile) => {
        this.userProfile = profile;
        this.loading = false;

        this.currentUserId = profile.id;
/*         
        console.log('Current User ID:', this.currentUserId);
        console.log('Current User Role:', this.currentUserRole); */

      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading profile:', error);
        this.error = 'Error al cargar el perfil';
        this.loading = false;
      }
    });
  }

  selectUser(user: User) {
    this.selectedUser = { ...user };
    this.updateDTO = {
      username: user.username
    };
    this.showModal = true;
  }

  updateUser() {
    if (!this.userProfile || !this.validateUpdate()) return;
/*     console.log('Usuarios: ', this.users);
    console.log('Usuario', this.userProfile?.username)
    console.log('ID Usuario', this.userProfile?.id) */
    console.log('Usuarios: ', this.users);
    if (this.users.some(u => u.username === this.updateDTO.username && u.id !== this.userProfile?.id)) {
      alert('El username ya existe. Por favor, elige otro.'); 
      this.loading = false;
      return;
    }


    this.loading = true;
    this.http.patch<User>(`${this.apiUrl}/users/user/${this.userProfile.id}`, this.updateDTO).subscribe({
      next: (updatedUser) => {
        this.auth.refreshSessionAfterUpdate(updatedUser, this.currentUserId!);
        
        this.userProfile = updatedUser;
        if (this.updateDTO.username) {
          this.username = this.updateDTO.username;
          // Store the new username if needed
          localStorage.setItem('username', this.username);
        }
        this.showModal = false;
        this.loading = false;
        this.updateDTO = {};
        this.showSuccessMessage('Perfil actualizado con éxito');
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error updating profile:', error);
        this.error = error.error?.message || 'Error al actualizar el perfil';
        this.loading = false;
      }
    });
  }

  private validateUpdate(): boolean {
    if (this.updateDTO.username && this.updateDTO.username.length < 3) {
      this.error = 'El username debe tener al menos 3 caracteres';
      return false;
    }
    if (this.updateDTO.password && this.updateDTO.password.length < 6) {
      this.error = 'La contraseña debe tener al menos 6 caracteres';
      return false;
    }
    return true;
  }

  private showSuccessMessage(message: string) {
    alert(message);
  }

  closeModal() {
    this.showModal = false;
    this.selectedUser = null;
    this.updateDTO = {};
    this.error = '';
  }
}