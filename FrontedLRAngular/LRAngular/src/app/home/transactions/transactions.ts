import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/auth.service';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgxPaginationModule } from 'ngx-pagination';
import { HttpErrorHandlerService } from '../../services/http-error-handler.service';

import * as ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';

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

interface NewTransaction {
  amount: number;
  description: string;
  accountName: string;
  accountType: string;
  categoryName: string;
}

interface TransactionUpdateDTO {
  amount?: number;
  description?: string;
  accountName?: string;
  accountType?: string;
  categoryName?: string;
}

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, NgxPaginationModule],
  templateUrl: './transactions.html',
  styleUrls: ['./transactions.css']
})
export class TransactionComponent implements OnInit {
  private apiUrl = 'http://localhost:8080/transaction';
  private accountApi = 'http://localhost:8080/account';
  private categoryApi = 'http://localhost:8080/category';

  username = '';
  transactions: Transaction[] = [];
  filteredTransactions: Transaction[] = [];
  newTransaction: NewTransaction = { amount: 0, description: '', accountName: '', accountType: '', categoryName: '' };
  selectedTransaction: Transaction | null = null;
  updateDTO: TransactionUpdateDTO = {};
  p = 1;
  itemsPerPage = 10;
  searchTerm = '';
  loading = false;
  showModal = false;
  errorMessage = '';

  // arrays para selects y mapas id -> nombre
  accounts: Account[] = [];
  accountNames: string[] = [];
  accountTypes: string[]=[];
  categories: Category[] = [];
  accountMap: Record<string, string> = {};
  accountTypeMap: Record<string, string> = {};
  categoryMap: Record<string, string> = {};

  constructor(private auth: AuthService, private http: HttpClient, private errorHandler: HttpErrorHandlerService) {}

  ngOnInit(): void {
    this.username = this.auth.getUsername() || '';
    this.loadAccounts();
    this.loadCategories();
    this.loadTransactions();
  }

  private loadAccounts(): void {
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
    this.http.get<any>(`${this.categoryApi}/categorys`).subscribe({
      next: res => {
        const list: Category[] = Array.isArray(res) ? res : (res?.data || res?.categorys || res?.categories || []);
        this.categories = list || [];
        this.categoryMap = {};
        (this.categories || []).forEach(c => {
          this.categoryMap[String(c.id)] = c.name;
          this.categoryMap[c.name] = c.name;
        });
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
    this.http.get<any>(`${this.apiUrl}/transactions`).subscribe({
      next: res => {
        const list: Transaction[] = Array.isArray(res) ? res : (res?.data || res?.transactions || []);
        this.transactions = list || [];
        this.filteredTransactions = [...this.transactions];
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        // this.handleError('Error al cargar transacciones', err);
        this.errorHandler.handle(err);
        this.loading = false;
      }
    });
  }

  createTransaction(): void {
    if (!this.validateNewTransaction(this.newTransaction)) return;
    this.loading = true;
    this.http.post(`${this.apiUrl}/create`, this.newTransaction, { responseType: 'text' as 'json' }).subscribe({
      next: () => {
        this.resetNewTransaction();
        this.loading = false;
        this.showSuccessMessage('Transacción creada con éxito');
        this.loadTransactions();
      },
      error: (err: HttpErrorResponse) => {
        alert('Datos Incorrectos o Fondos Insuficientes');
        // this.errorHandler.handle(err);
        this.loading = false;
      }
    });
  }

  selectTransaction(t: Transaction): void {
    this.selectedTransaction = { ...t };
    const accKey = String(t.accountId);
    const catKey = String(t.categoryId);
    this.updateDTO = {
      amount: t.amount,
      description: t.description,
      accountName: this.accountMap[accKey] || accKey,
      accountType: t.type,
      categoryName: this.categoryMap[catKey] || catKey
    };
    this.showModal = true;
  }

  updateTransaction(): void {
    if (!this.selectedTransaction) return;
    const payload: TransactionUpdateDTO = {};
    if (this.updateDTO.amount !== undefined && this.updateDTO.amount !== this.selectedTransaction.amount) payload.amount = this.updateDTO.amount;
    if (this.updateDTO.description && this.updateDTO.description !== this.selectedTransaction.description) payload.description = this.updateDTO.description;
    if (this.updateDTO.accountName && this.updateDTO.accountName !== this.selectedTransaction.accountId) payload.accountName = this.updateDTO.accountName;
    if (this.updateDTO.accountType && this.updateDTO.accountType !== this.selectedTransaction.type) payload.accountType = this.updateDTO.accountType;
    if (this.updateDTO.categoryName && this.updateDTO.categoryName !== this.selectedTransaction.categoryId) payload.categoryName = this.updateDTO.categoryName;
    if (Object.keys(payload).length === 0) {
      this.closeModal();
      return;
    }

    this.loading = true;
    this.http.patch<Transaction>(`${this.apiUrl}/update/${this.selectedTransaction.id}`, payload).subscribe({
      next: updated => {
        const idx = this.transactions.findIndex(t => t.id === updated.id);
        if (idx !== -1) this.transactions[idx] = updated;
        this.filteredTransactions = [...this.transactions];
        this.closeModal();
        this.loading = false;
        this.showSuccessMessage('Transacción actualizada con éxito');
      },
      error: (err: HttpErrorResponse) => {
        // this.handleError('Error al actualizar transacción', err);
        this.errorHandler.handle(err);
        this.loading = false;
      }
    });
  }

  searchTransactions(): void {
    if (!this.searchTerm.trim()) {
      this.filteredTransactions = [...this.transactions];
      return;
    }
    const term = this.searchTerm.toLowerCase();
    this.filteredTransactions = this.transactions.filter(t =>
      (t.description || '').toLowerCase().includes(term) ||
      (String(t.accountId) || '').toLowerCase().includes(term) ||
      (String(t.categoryId) || '').toLowerCase().includes(term) ||
      (t.type || '').toLowerCase().includes(term) ||
      t.id.toString().includes(term) ||
      t.amount.toString().includes(term)
    );
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

  private validateNewTransaction(n: NewTransaction): boolean {
    if (!n.amount || isNaN(n.amount)) {
      this.errorMessage = 'El monto es requerido';
      return false;
    }
    if (!n.description || n.description.trim().length === 0) {
      this.errorMessage = 'La descripción es requerida';
      return false;
    }
    if (!n.accountName || n.accountName.trim().length === 0) {
      this.errorMessage = 'El nombre de la cuenta es requerido';
      return false;
    }
    if (!n.accountType || n.accountType.trim().length === 0) {
      this.errorMessage = 'El tipo de cuenta es requerido';
      return false;
    }
    if (!n.categoryName || n.categoryName.trim().length === 0) {
      this.errorMessage = 'El nombre de la categoría es requerido';
      return false;
    }
    return true;
  }

  private resetNewTransaction(): void {
    this.newTransaction = { amount: 0, description: '', accountName: '', accountType: '', categoryName: '' };
    this.errorMessage = '';
  }



  private showSuccessMessage(message: string): void {
    alert(message);
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedTransaction = null;
    this.updateDTO = {};
    this.errorMessage = '';
  }

  downloadExcelReport(): void {
    if (!this.filteredTransactions || this.filteredTransactions.length === 0) {
      this.errorMessage = 'No tienes movimientos para realizar informe';
      setTimeout(() => (this.errorMessage = ''), 4000);
      return;
    }

    const username = this.auth.getUsername() || 'N/A';
    const today = new Date().toLocaleDateString();

    const workbook = new ExcelJS.Workbook();
    const sheet = workbook.addWorksheet('Movimientos');

    // Title (merge A1:C1)
    sheet.mergeCells('A1:C1');
    const titleCell = sheet.getCell('A1');
    titleCell.value = 'MAXSOFT-SISTEMA DE GESTION ADMINISTRATIVO PERSONAL V1.0';
    titleCell.alignment = { vertical: 'middle', horizontal: 'center' };
    titleCell.font = { name: 'Calibri', size: 12, bold: true };
    titleCell.fill = { type: 'pattern', pattern:'solid', fgColor:{ argb:'FFFFC000' } }; // orange/yellow
    titleCell.border = {
      top: { style: 'thin' }, left: { style: 'thin' }, bottom: { style: 'thin' }, right: { style: 'thin' }
    };


    // Info row (A2:C2)
    sheet.mergeCells('A2:C2');
    const infoCell = sheet.getCell('A2');
    infoCell.value = `Informe generado por: ${username}    Fecha: ${today}`;
    infoCell.alignment = { vertical: 'middle', horizontal: 'center' };
    infoCell.font = { name: 'Calibri', size: 10, italic: true };
    infoCell.fill = { type: 'pattern', pattern:'solid', fgColor:{ argb:'FFFFE699' } }; // light orange
    infoCell.border = {
      top: { style: 'thin' }, left: { style: 'thin' }, bottom: { style: 'thin' }, right: { style: 'thin' }
    };

    sheet.addRow([]);
    const header = sheet.addRow(['BankName', 'Type', 'Category', 'Amount', 'Date', 'Description']);
    // Aplica borde únicamente a A1, B1 y C1
    ['A4', 'B4', 'C4'].forEach(cell => {
      sheet.getCell(cell).border = {
        top: { style: 'thin' },
        left: { style: 'thin' },
        bottom: { style: 'thin' },
        right: { style: 'thin' }
      };
    });

    // Style header
    header.font = { bold: true, color: { argb: 'FF000000' } };
    header.alignment = { vertical: 'middle', horizontal: 'center' };
    header.eachCell((cell) => {
      cell.fill = { type: 'pattern', pattern:'solid', fgColor:{ argb:'FFD9EAF7' } }; // light blue
      cell.border = {
        top: { style: 'thin' }, left: { style: 'thin' }, bottom: { style: 'thin' }, right: { style: 'thin' }
      };
    });

    // Add data rows (start after headers)
    this.filteredTransactions.forEach((mov, index) => {
      const amount = mov.amount !== undefined && mov.amount !== null ? Number(mov.amount) : 0;
      const row = sheet.addRow([this.accountMap[mov.accountId] ?? '', mov.type ?? '',this.categoryMap[mov.categoryId] ?? '', amount, mov.date ?? '', mov.description ?? '']);
      // format balance as number with 2 decimals
      const amountCell = row.getCell(4);
      amountCell.numFmt = '#,##0.00';
      //FORMATO DE FECHA CON CASO DE NULL O UNDEFINED
      const dateCell = row.getCell(5);
      if (mov.date) {
        dateCell.value = new Date(mov.date);
        dateCell.numFmt = 'yyyy-mm-dd hh:mm:ss';
      } else {
        dateCell.value = ''; // o null
      }
      // alternate fill for better readability
      if (index % 2 === 1) {
        row.eachCell((cell) => {
          cell.fill = { type: 'pattern', pattern:'solid', fgColor:{ argb:'FFF3F3F3' } }; // light gray
        });
      }
      row.eachCell((cell) => {
        cell.border = {
          top: { style: 'thin' }, left: { style: 'thin' }, bottom: { style: 'thin' }, right: { style: 'thin' }
        };
      });
    });

    // Adjust column widths
    //'BankName', 'Type', 'Category', 'Amount', 'Date', 'Description'
    sheet.columns = [
      { key: 'bankname', width: 30 },
      { key: 'type', width: 18 },
      { key: 'category', width: 15 },
      { key: 'amount', width: 12 },
      { key: 'date', width: 30 },
      { key: 'description', width: 40 }
    ];

    // freeze header row
    sheet.views = [{ state: 'frozen', ySplit: 3 }];

    // Generate XLSX and download
    workbook.xlsx.writeBuffer().then((buffer) => {
      const blob = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      const filename = `transactions_${username}_report_${new Date().toISOString().slice(0, 10)}.xlsx`;
      saveAs(blob, filename);
    }).catch((err) => {
      console.error(err);
      this.errorMessage = 'Error generando el reporte';
      setTimeout(() => (this.errorMessage = ''), 4000);
    });
  }




}