// src/app/auth/auth.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode'; // 👈 Importa la librería



export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
}

@Injectable({
  providedIn: 'root'
})

export class AuthService {
  private API_URL = 'http://localhost:8080';

  constructor(private http: HttpClient, private router: Router) {}

  login(username: string, password: string): Observable<any> {
    return this.http.post(`${this.API_URL}/auth/login`, { username, password });
  }

  register(username: string, password: string): Observable<any> {
    return this.http.post(`${this.API_URL}/users/register`, { username, password });
  }

  // 👇 MÉTODO DE LOGOUT MEJORADO
  logout(): void {
    const token =this.getToken();
    // Es buena práctica llamar al endpoint de logout del backend
    // El interceptor añadirá el token a la cabecera automáticamente
    if (token) {
      this.http.post(`${this.API_URL}/auth/logout`, {}).subscribe({
        next: () => this.clearSession(),
        error: () => this.clearSession() // Limpia la sesión incluso si hay error
    });
  }else{
    this.clearSession();
  }
}



  // Método auxiliar para limpiar tokens y redirigir
  private clearSession(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    this.router.navigate(['/login']);
  }

 

  saveTokens(access: string, refresh: string) {
    localStorage.setItem('accessToken', access);
    localStorage.setItem('refreshToken', refresh);
  }

  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getRefreshToken(): string | null{
    return localStorage.getItem('refreshToken');
  }
  
  // 👇 NUEVO MÉTODO PARA OBTENER EL ROL
  getUserRole(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const decodedToken: any = jwtDecode(token);
      // Spring Security añade el prefijo "ROLE_". Lo quitamos para simplificar.
      // El nombre de la propiedad 'roles' debe coincidir con cómo lo generas en el backend.
      // A menudo es 'roles' o 'authorities'. Revisa tu JwtUtil.
      const roles: string[] = decodedToken.roles || [];
      if (roles.length > 0) {
        return roles[0].replace('ROLE_', '');
      }
      return null;
    } catch (error) {
      console.error("Error decodificando el token", error);
      return null;
    }
  }


  getUserRole2(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const decodedToken: any = jwtDecode(token);
      // Spring Security añade el prefijo "ROLE_". Lo quitamos para simplificar.
      // El nombre de la propiedad 'roles' debe coincidir con cómo lo generas en el backend.
      // A menudo es 'roles' o 'authorities'. Revisa tu JwtUtil.
      const roles: string = decodedToken.roles || null;

      return roles.replace('ROLE_', '');
    } catch (error) {
      console.error("Error decodificando el token", error);
      return null;
    }
  }


  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.getRefreshToken();
    // Recuerda que la propiedad en el JSON de respuesta del backend es 'accessToken'
    return this.http.post<AuthResponse>(`${this.API_URL}/auth/refresh`, { refreshToken })
      .pipe(
        tap((tokens: AuthResponse) => {
          this.saveTokens(tokens.accessToken, tokens.refreshToken);
        })
      );
  }




  getUsername(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const decodedToken: any = jwtDecode(token);


      const username: string = decodedToken.sub || '';
      
      if (username.length > 0) {
        return username;
      }
      return null;
    } catch (error) {
      console.error("Error decodificando el token", error);
      return null;
    }
  }


  
  refreshToken2(username: string, role: string): Observable<AuthResponse> {

    // Recuerda que la propiedad en el JSON de respuesta del backend es 'accessToken'
    return this.http.post<AuthResponse>(`${this.API_URL}/auth/newjwtwcmu`, { username, role})
      .pipe(
        tap((tokens: AuthResponse) => {
          /* console.log("Nuevo Username y role antes de guardar tokens de refreshtoken2:", this.getUsername(), this.getUserRole2()); */
          this.saveTokens(tokens.accessToken, tokens.refreshToken);
          /* console.log("Nuevo Username y role despues de guardar tokens de refreshtoken2:", this.getUsername(), this.getUserRole2()); */
        })
      );
  }

refreshSessionAfterUpdate(updatedUser: { id: number, username: string, role: string }, originalUserId: number): void {
  const currentUsername = this.getUsername();
  const currentRole = this.getUserRole();

  // 👇 Validamos si el user actualizado es el mismo que está logueado
  if (updatedUser.id === originalUserId) {
    // 👇 Solo si cambió username o role
    if (updatedUser.username !== currentUsername || updatedUser.role !== currentRole) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');

      // pedir nuevos tokens
      this.refreshToken2(updatedUser.username, updatedUser.role).subscribe({
        
        next: () => {
          
          const role = updatedUser.role;
          if (role === 'ROLE_ADMIN') {
            this.router.navigate(['/admin/users']);
            window.location.reload();

          } else if (role === 'ROLE_USER') {
            this.router.navigate(['/profile']);

          }
        },
        error: () => this.logout()
      });
    }
    }
  }

} 


