import { Component, OnInit } from '@angular/core';
import { CommonModule} from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { HttpClient } from '@angular/common/http';
import { NgxPaginationModule } from 'ngx-pagination';
import { FormsModule } from '@angular/forms';
interface BlacklistItem {
  id: number;
  token: string;
  createdAt: string;
}
@Component({
  selector: 'app-blacklist',
  imports: [CommonModule,
     RouterLink,
     NgxPaginationModule,
     FormsModule],
  templateUrl: './blacklist.html',
  styleUrl: './blacklist.css'
})
export class BlacklistComponent implements OnInit {
  username = ''; // Variable para almacenar el nombre de usuario

  blacklistData: BlacklistItem[] | null = null; // Variable para almacenar la blacklis
  p: number = 1; // Página actual
  itemsPerPage: number = 10; // Items por página
  searchTerm: string = ''; // Término de búsqueda

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
          this.blacklistData = response; // <-- ¡Aquí almacenamos la respuesta!
        },
        error: (error) => {
          console.error('Error al obtener la blacklist:', error);
          this.blacklistData = null; // Opcional: limpiar los datos en caso de error
        }
      });
  }

    // Filtrar datos
  get filteredData() {
    return this.blacklistData ? this.blacklistData.filter(item =>
      item.token.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      item.id.toString().includes(this.searchTerm) ||
      item.createdAt.toLowerCase().includes(this.searchTerm.toLowerCase())
    ) : [];
  }

  // Ver token completo
  showFullToken(token: string) {
    alert("JWT: "+token);
    
  }

}
