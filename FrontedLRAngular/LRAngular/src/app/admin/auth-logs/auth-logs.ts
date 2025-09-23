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
    this.getAllLogs();
  }

  getAllLogs() {
    this.http.get<AuditLog[]>('http://localhost:8080/admin/audit-log').subscribe({
      next: (data) => {
        this.logs = data;
        this.filteredLogs = data;
      },
      error: (error) => console.error('Error fetching logs:', error)
    });
  }

  filterByUser(username: string) {
    if (!username) {
      this.getAllLogs();
      return;
    }
    this.http.get<AuditLog[]>(`http://localhost:8080/admin/audit-log/user/${username}`).subscribe({
      next: (data) => {
        this.logs = data;
        this.filteredLogs = data;
      },
      error: (error) => console.error('Error filtering by user:', error)
    });
  }

  filterByAction(action: string) {
    if (!action) {
      this.getAllLogs();
      return;
    }
    this.http.get<AuditLog[]>(`http://localhost:8080/admin/audit-log/action/${action}`).subscribe({
      next: (data) => {
        this.logs = data;
        this.filteredLogs = data;
      },
      error: (error) => console.error('Error filtering by action:', error)
    });
  }


}