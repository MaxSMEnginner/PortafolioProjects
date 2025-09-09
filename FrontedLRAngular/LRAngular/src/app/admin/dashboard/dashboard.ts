// src/app/home/home.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/auth.service';
import { HttpClient } from '@angular/common/http'; // 👈 Importa HttpClient

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit { // 👈 Implementa OnInit
  
  welcomeMessage = 'Cargando...'; // Mensaje por defecto

  constructor(private auth: AuthService, private http: HttpClient) {}

  // ngOnInit se ejecuta cuando el componente se inicializa
  ngOnInit(): void {
    // El interceptor se encargará de añadir el token
    this.http.get('http://localhost:8080/admin/home', { responseType: 'text' })
      .subscribe({
        next: (message) => this.welcomeMessage = message,
        error: (err) => {
          console.error(err);
          this.welcomeMessage = 'Error al cargar el mensaje. ¿Tu token ha expirado?';
        }
      });
  }

  logout() {
    this.auth.logout();
  }
}