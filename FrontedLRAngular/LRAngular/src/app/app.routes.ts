// src/app/app.routes.ts

import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login';
import { RegisterComponent } from './auth/register/register';
import { HomeComponent } from './home/home';
import { DashboardComponent as AdminDashboard } from './admin/dashboard/dashboard'; // Renombramos para claridad
import { UsersComponent as AdminUsers } from './admin/users/users'; // Renombramos para claridad
import { AuthLogsComponent as AdminAuthLogs } from './admin/auth-logs/auth-logs'; // Renombramos para claridad
import { BlacklistComponent as AdminBL } from './admin/blacklist/blacklist'; // Renombramos para claridad
import { ProfileComponent as Profile} from './home/profile/profile'; // Renombramos para claridad
import { authGuard } from './auth/auth.guard';
import { adminGuard } from './auth/admin.guard';
import { AccountComponent as Account } from './home/accounts/accounts';
import { CategoryComponent as Category } from './home/categorys/categorys';
import { TransactionComponent as Transaction } from './home/transactions/transactions';






export const routes: Routes = [
  // Rutas públicas
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // Ruta para usuarios normales
  { 
    path: 'home', 
    component: HomeComponent, 
    canActivate: [authGuard] 
  },

  {
    path: 'home/profile',
    component: Profile,
    canActivate: [authGuard]
  },

  {
    path: 'home/accounts',
    component: Account,
    canActivate: [authGuard]
  },

  {
    path: 'home/categorys',
    component: Category,
    canActivate: [authGuard]
  },

  {
    path: 'home/transactions',
    component: Transaction,
    canActivate: [authGuard]
  },


  // Sección para administradores
  { 
    path: 'admin/dashboard', 
    component: AdminDashboard, 
    canActivate: [authGuard, adminGuard] // Requiere estar logueado Y ser admin
  },

  { 
    path: 'admin/users', 
    component: AdminUsers, 
    canActivate: [authGuard, adminGuard] // Requiere estar logueado Y ser admin
  },
  { 
    path: 'admin/auth-logs', 
    component: AdminAuthLogs, 
    canActivate: [authGuard, adminGuard] // Requiere estar logueado Y ser admin
  },
  { 
    path: 'admin/blacklist', 
    component: AdminBL, 
    canActivate: [authGuard, adminGuard] // Requiere estar logueado Y ser admin
  },

  // Redirecciones
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];