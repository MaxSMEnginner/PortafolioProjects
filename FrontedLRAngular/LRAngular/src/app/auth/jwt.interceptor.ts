import { HttpErrorResponse, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from "@angular/common/http";
import { inject } from "@angular/core";
import { catchError, switchMap, throwError } from "rxjs";
import { AuthService } from "./auth.service";
import { HttpErrorHandlerService } from "../services/http-error-handler.service";


export const JwtInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const authService = inject(AuthService);
  const token = authService.getToken();
  const errorHandler = inject(HttpErrorHandlerService);

  // 🔹 Agrega el token si existe
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(req).pipe(
    catchError((error: any) => {
      // 🔹 Solo actuamos si el error viene del servidor
      if (error instanceof HttpErrorResponse) {
        // ✅ Caso 1: Token inválido o expirado → 403
        if (error.status === 403 && !req.url.includes('/auth/refresh')) {
          return authService.refreshToken().pipe(
            switchMap((tokens) => {
              const newReq = req.clone({
                setHeaders: {
                  Authorization: `Bearer ${tokens.accessToken}`,
                },
              });
              return next(newReq);
            }),
            catchError((refreshError) => {
              errorHandler.handle(refreshError);
              return throwError(() => refreshError);
            })
          );
        }

      }

      // 🔹 Relanza otros errores sin tocar nada
      return throwError(() => error);
    })
  );
};
