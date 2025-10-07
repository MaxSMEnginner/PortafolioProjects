import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth.service';



@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent {
  username = '';
  password = '';

  constructor(private auth: AuthService, private router: Router) {}

    private validateUser(username: string, password: string): boolean {
    if (!username || username.length < 3) {
      
      return false;
    }
    if (!password || password.length < 6) {

      return false;
    }
    return true;
  }

  onSubmit() {
    if (!this.validateUser(this.username, this.password)) {
      alert('❌ Usuario o contraseña no cumplen los requisitos mínimos');
      return;
    }else{
       this.auth.register(this.username, this.password).subscribe({
      next: () => {
        alert('✅ Registro exitoso, ahora inicia sesión');
        this.router.navigate(['/login']);
      },
      error: () => {alert('❌ Error al crear usuario'), this.router.navigate(['/register'])}

    });

    }


   
  }
}
