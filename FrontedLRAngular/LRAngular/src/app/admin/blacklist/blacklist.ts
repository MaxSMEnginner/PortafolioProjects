import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { HttpClient } from '@angular/common/http'; // 👈 Importa HttpClient


interface BlacklistItem {
  id: number;
  token: string;
  createdAt: string;
}
@Component({
  selector: 'app-blacklist',
  imports: [CommonModule, RouterLink],
  templateUrl: './blacklist.html',
  styleUrl: './blacklist.css'
})
export class BlacklistComponent implements OnInit {
  username = ''; // Variable para almacenar el nombre de usuario

  blacklistData: BlacklistItem[] | null = null; // Variable para almacenar la blacklis
  // t
  constructor(private auth: AuthService, private http: HttpClient) {
    
  }
  ngOnInit(): void {
    this.username = this.auth.getUsername() || '';
    this.showBlacklist();
  }
 showBlacklist() {
    this.http.get<BlacklistItem[]>('http://localhost:8080/admin/black-list', { withCredentials: true })
      .subscribe({
        next: (response) => {
          console.log('JWT Blacklist:', response);
          this.blacklistData = response; // <-- ¡Aquí almacenamos la respuesta!
        },
        error: (error) => {
          console.error('Error al obtener la blacklist:', error);
          this.blacklistData = null; // Opcional: limpiar los datos en caso de error
        }
      });
  }

}
