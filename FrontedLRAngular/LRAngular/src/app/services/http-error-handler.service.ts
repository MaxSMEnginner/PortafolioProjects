import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class HttpErrorHandlerService {

  handle(error: HttpErrorResponse): void {
    let message = 'Ocurrió un error inesperado.';

    switch (error.status) {
      case 400:
        if (error.error?.error) {
            message = `Solicitud inválida: ${error.error.error}`;
        } else {
        message = 'Solicitud inválida (400).';
        }
        break;
      case 401:
        if (error.error?.error) {
            message = `No estás autorizado: ${error.error.error}`;
        } else {
          message = 'No estás autorizado. Inicia sesión nuevamente.';
        }
        break;
      case 403:
        if (error.error?.error) {
            message = `Acceso denegado: ${error.error.error}`;
        } else {
            message = 'Acceso denegado (403): No tienes permisos para realizar esta acción.';
        }
        break;
      case 404:
        if (error.error?.error) {
            message = `Recurso no encontrado: ${error.error.error}`;
        }else{
        message = 'El recurso solicitado no existe.';
        }
        break;
      case 409:
        if (error.error?.error) {
          
        }else{
        message = error.error?.error || 'Conflicto: el registro ya existe.';
        }
        break;

      case 500:
        if (error.error?.error) { 
            message = `Error interno del servidor: ${error.error.error}`;
        }else{
        message = 'Error interno del servidor. Intenta más tarde.';
        }
        break;
      default:
        message = `Error desconocido (${error.status}).`;
    }

    alert(message); // <- Esto genera la ventana tipo “localhost:4200 dice: ...”
    console.error('Detalles del error:', error);
  }
}
