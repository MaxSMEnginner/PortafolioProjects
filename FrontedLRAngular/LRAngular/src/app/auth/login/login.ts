import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  username = '';
  password = '';
  isLoading = false;

  constructor(private auth: AuthService, private router: Router) {}

  onSubmit() {
    if (!this.username || !this.password) return;
    
    this.isLoading = true;
    this.auth.login(this.username, this.password).subscribe({
      next: (res) => {
        this.auth.saveTokens(res.accessToken, res.refreshToken);
        const role = this.auth.getUserRole();
        
        if (role === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else if (role === 'USER') {
          this.router.navigate(['/home']);
        } else {
          this.router.navigate(['/login']);
        }
      },
      error: () => {
        alert('❌ Usuario o contraseña incorrectos');
        this.isLoading = false;
      }
    });
  }
}