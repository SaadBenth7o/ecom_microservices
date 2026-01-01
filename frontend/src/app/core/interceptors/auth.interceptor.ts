import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  // #region agent log
  fetch('http://127.0.0.1:7242/ingest/4d728488-9655-4fb7-9ad8-610057eb6692',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'auth.interceptor.ts:8',message:'Interceptor called',data:{url:req.url,hasToken:!!token,tokenLength:token?.length||0},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'A'})}).catch(()=>{});
  // #endregion

  // Add token to all requests
  if (token) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    
    // #region agent log
    fetch('http://127.0.0.1:7242/ingest/4d728488-9655-4fb7-9ad8-610057eb6692',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'auth.interceptor.ts:18',message:'Request with token',data:{url:req.url,tokenPreview:token.substring(0,20)+'...'},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'A'})}).catch(()=>{});
    // #endregion
    
    return next(cloned).pipe(
      catchError((error: HttpErrorResponse) => {
        // #region agent log
        fetch('http://127.0.0.1:7242/ingest/4d728488-9655-4fb7-9ad8-610057eb6692',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'auth.interceptor.ts:25',message:'HTTP error with token',data:{url:req.url,status:error.status,statusText:error.statusText,errorMessage:error.message,errorBody:error.error},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:error.status===401?'B':error.status===404?'C':error.status===0?'D':'E'})}).catch(()=>{});
        // #endregion
        
        console.error('❌ HTTP Error:', error);
        console.error('📍 Request URL:', req.url);
        console.error('📊 Status:', error.status, error.statusText);
        console.error('💬 Error message:', error.message);
        if (error.error) {
          console.error('📄 Error body:', error.error);
        }
        
        return throwError(() => error);
      })
    );
  }

  // #region agent log
  fetch('http://127.0.0.1:7242/ingest/4d728488-9655-4fb7-9ad8-610057eb6692',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'auth.interceptor.ts:48',message:'No token available',data:{url:req.url,isAuthenticated:authService.isAuthenticated()},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'A'})}).catch(()=>{});
  // #endregion

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // #region agent log
      fetch('http://127.0.0.1:7242/ingest/4d728488-9655-4fb7-9ad8-610057eb6692',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'auth.interceptor.ts:53',message:'HTTP error without token',data:{url:req.url,status:error.status,statusText:error.statusText},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'A'})}).catch(()=>{});
      // #endregion
      
      console.error('❌ HTTP Error (no token):', error);
      return throwError(() => error);
    })
  );
};

