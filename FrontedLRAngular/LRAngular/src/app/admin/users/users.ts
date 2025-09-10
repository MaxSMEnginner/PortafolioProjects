import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/auth.service';
import { HttpClient } from '@angular/common/http'; // 👈 Importa HttpClient
import {RouterLink} from '@angular/router'; 

@Component({
  selector: 'app-users',
  imports: [CommonModule,RouterLink],
  standalone: true,
  templateUrl: './users.html',
  styleUrl: './users.css'
})
export class UsersComponent implements OnInit {
  username = ''; // Variable para almacenar el nombre de usuario

  constructor(private auth: AuthService, private http: HttpClient) {}
  

  ngOnInit(): void {
    this.username = this.auth.getUsername() || '';  
    
  }



}
