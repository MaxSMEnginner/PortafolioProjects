import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/auth.service';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgxPaginationModule } from 'ngx-pagination';
import { HttpErrorHandlerService } from '../../services/http-error-handler.service';

//exceldocimports
import * as ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';

interface Account {
  id: number;
  name: string;
  type: string;
  currentBalance: number;
}

interface NewAccount {
  id: number;
  name: string;
  type: string;
  currentBalance: number;
}

interface AccountUpdateDTO {
  name?: string;
  type?: string;
  currentBalance?: number;
}

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, NgxPaginationModule],
  templateUrl: './accounts.html',
  styleUrls: ['./accounts.css']
})
export class AccountComponent implements OnInit {
  private apiUrl = 'http://localhost:8080/account';

  username = '';
  accounts: Account[] = [];
  filteredAccounts: Account[] = [];
  newAccount: NewAccount = { id: 0, name: '', type: '', currentBalance: 0 };
  selectedAccount: Account | null = null;
  updateDTO: AccountUpdateDTO = {};
  p = 1;
  itemsPerPage = 10;
  searchTerm = '';
  loading = false;
  showModal = false;
  errorMessage = '';

  constructor(private auth: AuthService, private http: HttpClient, private errorHandler: HttpErrorHandlerService) {}

  ngOnInit(): void {
    this.username = this.auth.getUsername() || '';
    this.loadAccounts();
  }

  loadAccounts() {
    this.loading = true;
    this.http.get<Account[]>(`${this.apiUrl}/accounts`).subscribe({
      next: (data) => {
        this.accounts = data;
        this.filteredAccounts = [...data];
        this.loading = false;
      },
      error: (error : HttpErrorResponse) => {
        this.errorHandler.handle(error);
        this.loading = false;
      }
    });
  }

  createAccount() {
    if (!this.validateNewAccount(this.newAccount)) return;
    this.loading = true;

    this.http.post(`${this.apiUrl}/create`, this.newAccount, { responseType: 'text' }).subscribe({
       next: () => {
         this.resetNewAccount();
         this.loading = false;
         this.showSuccessMessage('Cuenta creada con éxito');
         this.loadAccounts();
       },
       error: (error: HttpErrorResponse) => {
         this.errorHandler.handle(error); 
         this.loading = false;
       }
     });
  }

  selectAccount(acc: Account) {
    this.selectedAccount = { ...acc };
    this.updateDTO = {
      name: acc.name,
      type: acc.type,
      currentBalance: acc.currentBalance
    };
    this.showModal = true;
  }

  updateAccount() {
    if (!this.selectedAccount) return;
    if (!this.validateUpdate()) return;

    const payload: AccountUpdateDTO = this.prepareUpdateData();
    if (Object.keys(payload).length === 0) {
      this.closeModal();
      return;
    }

    this.loading = true;
    this.http.patch<Account>(`${this.apiUrl}/update/${this.selectedAccount.id}`, payload).subscribe({
      next: (updated) => {
        const idx = this.accounts.findIndex(a => a.id === updated.id);
        if (idx !== -1) this.accounts[idx] = updated;
        this.filteredAccounts = [...this.accounts];
        this.closeModal();
        this.loading = false;
        this.showSuccessMessage('Cuenta actualizada con éxito');
      },
      error: (error: HttpErrorResponse) => {
        this.errorHandler.handle(error);
        this.loading = false;
      }
    });
  }

  deleteAccount(id: number) {
    if (!confirm('¿Estás seguro de eliminar esta cuenta?')) return;
    this.loading = true;
    this.http.delete(`${this.apiUrl}/delete/${id}`).subscribe({
      next: () => {
        this.accounts = this.accounts.filter(a => a.id !== id);
        this.filteredAccounts = [...this.accounts];
        this.loading = false;
        this.showSuccessMessage('Cuenta eliminada con éxito');
      },
      error: () => {
        this.loading = false;

      

      }
    });
  }

  searchAccounts() {
    if (!this.searchTerm.trim()) {
      this.filteredAccounts = [...this.accounts];
      return;
    }
    const term = this.searchTerm.toLowerCase();
    this.filteredAccounts = this.accounts.filter(a =>
      a.name.toLowerCase().includes(term) ||
      a.type.toLowerCase().includes(term) ||
      a.currentBalance.toString().includes(term)
    );
    this.p = 1;  
  }

  private validateNewAccount(acc: NewAccount): boolean {
    if (!acc.name || acc.name.trim().length === 0) {
      this.errorMessage = 'El nombre es requerido';
      return false;
    }
    if (!acc.type || acc.type.trim().length === 0) {
      this.errorMessage = 'El tipo es requerido';
      return false;
    }
    return true;
  }

  private validateUpdate(): boolean {
    if (this.updateDTO.name !== undefined && this.updateDTO.name.trim().length === 0) {
      this.errorMessage = 'El nombre no puede estar vacío';
      return false;
    }
    return true;
  }

  private prepareUpdateData(): AccountUpdateDTO {
    const out: AccountUpdateDTO = {};
    if (this.updateDTO.name && this.updateDTO.name !== this.selectedAccount?.name) out.name = this.updateDTO.name;
    if (this.updateDTO.type && this.updateDTO.type !== this.selectedAccount?.type) out.type = this.updateDTO.type;
    if (this.updateDTO.currentBalance !== undefined && this.updateDTO.currentBalance !== this.selectedAccount?.currentBalance) out.currentBalance = this.updateDTO.currentBalance;
    return out;
  }

  private resetNewAccount() {
    this.newAccount = { id: 0, name: '', type: '', currentBalance: 0 };
    this.errorMessage = '';
  }



  private showSuccessMessage(message: string) {
    this.errorMessage = '';
    alert(message);
  }

  closeModal() {
    this.showModal = false;
    this.selectedAccount = null;
    this.updateDTO = {};
    this.errorMessage = '';
  }


  downloadExcelReport(): void {
    if (!this.filteredAccounts || this.filteredAccounts.length === 0) {
      this.errorMessage = 'No tienes cuentas para realizar informe';
      setTimeout(() => (this.errorMessage = ''), 4000);
      return;
    }

    const username = this.auth.getUsername() || 'N/A';
    const today = new Date().toLocaleDateString();

    const workbook = new ExcelJS.Workbook();
    const sheet = workbook.addWorksheet('Cuentas');

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
    const header = sheet.addRow(['Name', 'Type', 'Balance']);
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
    this.filteredAccounts.forEach((acc, index) => {
      const balanceNum = acc.currentBalance !== undefined && acc.currentBalance !== null ? Number(acc.currentBalance) : 0;
      const row = sheet.addRow([acc.name ?? '', acc.type ?? '', balanceNum]);
      // format balance as number with 2 decimals
      const balanceCell = row.getCell(3);
      balanceCell.numFmt = '#,##0.00';
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
    sheet.columns = [
      { key: 'name', width: 30 },
      { key: 'type', width: 18 },
      { key: 'balance', width: 15 }
    ];

    // freeze header row
    sheet.views = [{ state: 'frozen', ySplit: 3 }];

    // Generate XLSX and download
    workbook.xlsx.writeBuffer().then((buffer) => {
      const blob = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      const filename = `accounts_${username}_report_${new Date().toISOString().slice(0, 10)}.xlsx`;
      saveAs(blob, filename);
    }).catch((err) => {
      console.error(err);
      this.errorMessage = 'Error generando el reporte';
      setTimeout(() => (this.errorMessage = ''), 4000);
    });
  }
  logout() {
    this.auth.logout();
  }



}