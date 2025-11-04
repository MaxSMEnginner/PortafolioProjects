// ...existing code...
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth/auth.service';
import { HttpClient } from '@angular/common/http'; // 👈 Importa HttpClient
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent implements OnInit {
  welcomeMessage = 'Cargando...';
  username = '';
  isAdmin = false; 
  role = 'ROLE_USERS';
  constructor(private auth: AuthService, private http: HttpClient) {}

  ngOnInit(): void {

    this.username = this.auth.getUsername() || '';
    this.role = this.auth.getUserRole() || 'ROLE_USER';
    //console.log('User role:', this.role);

    // Determina si es admin de forma defensiva según lo que exponga AuthService
    this.isAdmin = (() => {
      if ("ADMIN" === this.role) {
        return true;
      }
      return false;
    })();
    //console.log('Is Admin:', this.isAdmin);

    this.http.get('http://localhost:8080/users/home', { responseType: 'text' })
      .subscribe({
        next: (message) => this.welcomeMessage = message.replace('🚀',''),
        error: (err) => {
          console.error(err);
          this.welcomeMessage = 'Error al cargar el mensaje. ¿Tu token ha expirado?';
        }
      });
  }

  logout() {
    this.auth.logout();
  }

  gotoadmin(){
    window.location.href = '/admin/dashboard';
  }

}