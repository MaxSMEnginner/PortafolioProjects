// src/app/auth/admin.guard.ts

import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn() && (authService.getUserRole() === 'ADMIN' || authService.getUserRole2() === 'ADMIN')) {
    return true; 
  }

  // Si no es admin, lo rediriges a su home o al login
  router.navigate(['/home']); 
  return false;
};