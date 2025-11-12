import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { NgxPaginationModule } from 'ngx-pagination';
import { AuthService } from '../../auth/auth.service';

interface AuditLog {
  id: number;
  username: string;
  action: string;
  timestamp: string;
  ip: string;
  ip_address: string;
}

@Component({
  selector: 'app-auth-logs',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, NgxPaginationModule],
  templateUrl: './auth-logs.html',
  styleUrl: './auth-logs.css'
})
export class AuthLogsComponent implements OnInit {
  logs: AuditLog[] = [];
  filteredLogs: AuditLog[] = [];
  searchTerm: string = '';
  selectedAction: string = '';
  selectedUser: string = '';
  p: number = 1;
  itemsPerPage: number = 10;
  username = '';

  constructor(private http: HttpClient,
              private auth: AuthService
  ) {}

  ngOnInit() {
    this.username = this.auth.getUsername() || '';
    this.loadLogs();
  }

  loadLogs() {
    this.http.get<any>('http://localhost:8080/admin/audit-log').subscribe({
      next: (data) => {
        const list: any[] = Array.isArray(data) ? data : (data?.data || data?.logs || data?.auditLogs || []);
        // Normalizar cada log para asegurar que las propiedades existen
        this.logs = (list || []).map(l => ({
          id: l?.id ?? 0,
          username: l?.username ?? '',
          action: l?.action ?? '',
          timestamp: l?.timestamp ?? '',
          ip: l?.ip ?? '',
          ip_address: l?.ip_address ?? 'localhost'
        }));
        this.filteredLogs = [...this.logs];
        this.p = 1;
      },
      error: (error) => console.error('Error fetching logs:', error)
    });
  }

    searchLogs() {
    if (!this.searchTerm.trim()) {
      this.filteredLogs = [...this.logs];
      this.p=1;// volver a la primera página
      return;
    }

    const searchTerm = this.searchTerm.toLowerCase();
    this.filteredLogs = this.logs.filter(log =>
      log.username.toLowerCase().includes(searchTerm) ||
      log.action.toLowerCase().includes(searchTerm) ||
      log.id.toString().includes(searchTerm)||
      log.ip.toLowerCase().includes(searchTerm)||
      log.ip_address.toLowerCase().includes(searchTerm)||
      log.timestamp.toLowerCase().includes(searchTerm)
      
    );
    this.p=1;
  }


  logout() {
    this.auth.logout();
  }
}