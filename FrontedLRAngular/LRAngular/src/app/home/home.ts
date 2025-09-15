// src/app/home/home.component.ts

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
export class HomeComponent implements OnInit { // 👈 Implementa OnInit
  
 
  /* menganita = App.menganita; */
  welcomeMessage = 'Cargando...'; // Mensaje por defecto
  username = ''; // Variable para almacenar el nombre de usuario

  constructor(private auth: AuthService, private http: HttpClient) {}

  // ngOnInit se ejecuta cuando el componente se inicializa
  ngOnInit(): void {
    this.username = this.auth.getUsername() || '';  
    
    // El interceptor se encargará de añadir el token
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

}