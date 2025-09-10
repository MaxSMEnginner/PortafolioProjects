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

  constructor(private auth: AuthService, private router: Router) {}

  onSubmit() {
    this.auth.login(this.username, this.password).subscribe({
      next: (res) => {
        this.auth.saveTokens(res.accessToken, res.refreshToken);

        // 👇 LÓGICA DE REDIRECCIÓN BASADA EN ROL
        const role = this.auth.getUserRole();
        /* console.log('jwt:',this.auth.getToken()); */
        /* this.router.navigate(['/admin/dashboard']); */ // Redirige a home independientemente del rol
         if (role === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']); // Ruta para admin
        } else if (role === 'USER') {
          this.router.navigate(['/home']); // Ruta para user
        } else {
          // Por si acaso el rol no es ninguno de los esperados
          this.router.navigate(['/login']);
        } 
      },
      error: () => alert('❌ Usuario o contraseña incorrectos')
    });
  }
}