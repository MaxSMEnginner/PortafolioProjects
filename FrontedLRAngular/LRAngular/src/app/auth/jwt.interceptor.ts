// src/app/auth/jwt.interceptor.ts

import { HttpErrorResponse, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from "@angular/common/http";
import { inject } from "@angular/core";
import { catchError, switchMap, throwError } from "rxjs";
import { AuthService } from "./auth.service";

export const JwtInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const authService = inject(AuthService);
  const token = authService.getToken(); // Llama al servicio para obtener el token

  // Clona la petición para añadir la cabecera de autorización si el token existe
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  // Envía la petición y se prepara para capturar errores
  return next(req).pipe(
    catchError((error: any) => {
      // Si el error es 401 (token expirado) y no estamos ya en el endpoint de refresh
      if (error instanceof HttpErrorResponse && error.status === 403 && !req.url.includes('/auth/refresh')) {
        
        // Intenta refrescar el token
        return authService.refreshToken().pipe(
          switchMap((tokens) => {
            // Si el refresco es exitoso, clona la petición original con el NUEVO token
            const newReq = req.clone({
              setHeaders: {
                Authorization: `Bearer ${tokens.accessToken}`
              }
            });
            // Reintenta la petición original que había fallado
            return next(newReq);
          }),
          catchError((refreshError) => {
            // Si el refresco de token también falla, la sesión ha terminado
            authService.logout();
            return throwError(() => refreshError);
          })
        );
      }
      
      // Para cualquier otro error, simplemente lo relanzamos
      return throwError(() => error);
    })
  );
};