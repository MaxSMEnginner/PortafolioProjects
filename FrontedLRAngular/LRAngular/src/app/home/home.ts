// ...existing code...
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth/auth.service';
import { HttpClient, HttpErrorResponse } from '@angular/common/http'; // 👈 Importa HttpClient
import { RouterLink } from '@angular/router';
import { HttpErrorHandlerService } from '../services/http-error-handler.service';
import { NgxPaginationModule } from 'ngx-pagination';
import { FormsModule } from '@angular/forms';
import { ColdObservable } from 'rxjs/internal/testing/ColdObservable';

interface Transaction {
  id: number;
  amount: number;
  description: string;
  accountId: string; // id o nombre según API
  type: string;
  categoryId: string;
  date?: string;
}

interface Account {
  id: number;
  name: string;
  type: string;
}

interface Category {
  id: number;
  name: string;
}


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, NgxPaginationModule, FormsModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent implements OnInit {
  private transactionApi = 'http://localhost:8080/transaction';
  private accountApi = 'http://localhost:8080/account';
  private categoryApi = 'http://localhost:8080/category';

  username = '';
  isAdmin = false; 
  role = 'ROLE_USERS';
  
  // Variables para transacciones y búsqueda
  transactions: Transaction[] = [];
  filteredTransactions: Transaction[] = [];
  searchTerm = '';
  loading = false;
  //
  accounts: Account[] = [];
  filteredAccounts: Account[] = [];
  accountNames: string[] = [];
  accountTypes: string[]=[];
  accountMap: Record<string, string> = {};  
  accountTypeMap: Record<string, string> = {};
  //
  categories: Category[] = [];
  filteredCategories: Category[] = [];
  categoryMap: Record<string, string> = {};
  //
  p = 1;
  itemsPerPage = 10;
  //KPIS
  totalTransactions: number = 0;
  totalIncome: number = 0;
  totalExpense: number = 0;
  avgAmount: number = 0;
  totalAccounts: number = 0;
  totalCategories: number = 0;


  
  constructor(private auth: AuthService, private http: HttpClient, private errorHandler: HttpErrorHandlerService) {}

  ngOnInit(): void {

    this.username = this.auth.getUsername() || '';
    this.role = this.auth.getUserRole() || 'ROLE_USER';
    this.loadAccounts();
    this.loadCategories();
    this.loadTransactions();
    //console.log('User role:', this.role);

    // Determina si es admin de forma defensiva según lo que exponga AuthService
    this.isAdmin = (() => {
      if ("ADMIN" === this.role) {
        return true;
      }
      return false;
    })();
    //console.log('Is Admin:', this.isAdmin);

    //KPIS



  }

//KPIS
updateKPIs(): void {
    const tx = (this.filteredTransactions ?? this.transactions ?? []) as any[];
    
    const amounts = tx.map(t => Number(t.amount ?? 0));
    const total = amounts.reduce((s, v) => s + v, 0);
    
    
    const tx_income=tx
      .filter(t => (t.type || '').toString().toLowerCase().toString().toLowerCase().includes('inc'));
    const tx_expense=tx
      .filter(t => (t.type || '').toString().toLowerCase().toString().toLowerCase().includes('exp'));

    // Ajusta según la propiedad 'type' de tus transacciones ('income'/'expense' o 'Ingreso'/'Gasto')
    this.totalIncome = tx_income.reduce((s, t) => s + Number(t.amount ?? 0), 0);
    // console.log('Total Income: ', this.totalIncome);
    // console.log('longitud tx_income: ',tx_income.length);
    this.avgAmount = tx_income.length ? this.totalIncome / tx_income.length : 0;

    this.totalExpense = tx_expense
      .reduce((s, t) => s + Number(t.amount ?? 0), 0);

    this.totalTransactions = tx_income.length + tx_expense.length;
    // console.log('tx_income:' ,tx_income.length);
    // console.log('tx_expense:' ,tx_expense.length);
    // console.log('totalTransactions:' ,this.totalTransactions);

    this.totalAccounts = (this.filteredAccounts ?? []).length;
    console.log('-----------------------------------------')
    console.log('accounts:' ,this.filteredAccounts);
    console.log('totalAccounts:' ,this.filteredAccounts.length);
    console.log('-----------------------------------------')
    this.totalCategories = (this.categories ?? []).length;
  }


//


  logout() {
    this.auth.logout();
  }

  gotoadmin(){
    window.location.href = '/admin/dashboard';
  }


  searchTransactions(): void {
    if (!this.searchTerm.trim()) {
      this.filteredTransactions = [...this.transactions];
      this.p = 1;               // volver a la primera página
      this.updateKPIs();       // actualizar KPIs al mostrar todos
      return;
    }
    const term = this.searchTerm.toLowerCase();
    this.filteredTransactions = this.transactions.filter(t =>
      (t.description || '').toLowerCase().includes(term) ||
      (String(this.accountMap[t.accountId]) || '').toLowerCase().includes(term) ||
      (String(this.accountTypeMap[t.accountId]) || '').toLowerCase().includes(term) ||
      (String(this.categoryMap[t.categoryId]) || '').toLowerCase().includes(term) ||
      (t.type || '').toLowerCase().includes(term) ||
      (t.date || '').toLowerCase().includes(term) ||
      t.amount.toString().includes(term)
    );
    this.filteredAccounts = this.accounts.filter(a =>
      (a.name || '').toLowerCase().includes(term) ||
      (a.type || '').toLowerCase().includes(term)
    );
    this.filteredCategories = this.categories.filter(c =>
      (c.name || '').toLowerCase().includes(term)
    );  
    this.p = 1;                 // opcional: volver a la primera página tras filtrar
    this.updateKPIs();
  }

  private loadAccounts(): void {
    this.loading=true;
      this.http.get<any>(`${this.accountApi}/accounts`).subscribe({
        next: res => {
          const list: Account[] = Array.isArray(res) ? res : (res?.data || res?.accounts || []);
          this.accounts = list || [];
          const names = Array.from(new Set(this.accounts.map(a => a.name)));
          const types= Array.from(new Set(this.accounts.map(a => a.type)));
          this.accountNames = names;
          this.accountTypes=types;
  
          //console.log(this.accountNames);
          this.accountMap = {};
          (this.accounts || []).forEach(a => {
            this.accountMap[String(a.id)] = a.name;
            this.accountMap[a.name] = a.name;
          });
          this.accountTypeMap={};
          (this.accounts || []).forEach(a=>{
            this.accountTypeMap[String(a.id)]=a.type;
            this.accountTypeMap[a.type]=a.type;
          });
          this.filteredAccounts = [...this.accounts];
          this.loading=false;
          this.updateKPIs();
        },
        error: (err: HttpErrorResponse) => {
          // console.warn('[Transactions] loadAccounts error:', err);
          this.errorHandler.handle(err);
          this.accounts = [];
          this.accountMap = {};
          this.accountTypeMap={};
  
        }
      });
    }
  
  private loadCategories(): void {
    this.loading=true;
      this.http.get<any>(`${this.categoryApi}/categorys`).subscribe({
        next: res => {
          const list: Category[] = Array.isArray(res) ? res : (res?.data || res?.categorys || res?.categories || []);
          this.categories = list || [];
          this.categoryMap = {};
          (this.categories || []).forEach(c => {
            this.categoryMap[String(c.id)] = c.name;
            this.categoryMap[c.name] = c.name;
          });
          this.filteredCategories = [...this.categories];
          this.loading=false;
          this.updateKPIs();
        },
        error: (err: HttpErrorResponse) => {
          // console.warn('[Transactions] loadCategories error:', err);
          this.errorHandler.handle(err);
          this.categories = [];
          this.categoryMap = {};
        }
      });
    }
  
  loadTransactions(): void {
      this.loading = true;
      this.http.get<any>(`${this.transactionApi}/transactions`).subscribe({
        next: res => {
          const list: Transaction[] = Array.isArray(res) ? res : (res?.data || res?.transactions || []);
          this.transactions = list || [];
          this.filteredTransactions = [...this.transactions];
          this.loading = false;
          this.updateKPIs();
        },
        error: (err: HttpErrorResponse) => {
          // this.handleError('Error al cargar transacciones', err);
          this.errorHandler.handle(err);
          this.loading = false;
        }
      });
    }
  accountName(idOrName: string | number): string {
    const key = String(idOrName);
    return this.accountMap[key] || key;
  }

  accountType(idOrName: string | number): string {
    const key = String(idOrName);
    return this.accountTypeMap[key] || key;
  }

  categoryName(idOrName: string | number): string {
    const key = String(idOrName);
    return this.categoryMap[key] || key;
  }

}