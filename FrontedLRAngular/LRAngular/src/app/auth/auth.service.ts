import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private API_URL = 'http://localhost:8080'; // tu backend Spring Boot

  constructor(private http: HttpClient, private router: Router) {}

  login(username: string, password: string): Observable<any> {
    return this.http.post(`${this.API_URL}/auth/login`, { username, password });
  }

  register(username: string, password: string): Observable<any> {
    return this.http.post(`${this.API_URL}/users/register`, { username, password });
  }

  logout(): void {
    localStorage.removeItem('jwt');
    localStorage.removeItem('refreshToken');
    this.router.navigate(['/login']);
  }

  saveTokens(access: string, refresh: string) {
    localStorage.setItem('jwt', access);
    localStorage.setItem('refreshToken', refresh);
  }

  getToken(): string | null {
    return localStorage.getItem('jwt');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
