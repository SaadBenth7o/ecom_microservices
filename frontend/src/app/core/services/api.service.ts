import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    private baseUrl = 'http://localhost:8888';

    constructor(private http: HttpClient) { }

    get<T>(serviceName: string, endpoint: string): Observable<T> {
        const url = `${this.baseUrl}/${serviceName}${endpoint}`;
        
        // #region agent log
        fetch('http://127.0.0.1:7242/ingest/4d728488-9655-4fb7-9ad8-610057eb6692',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'api.service.ts:14',message:'API GET request',data:{url,serviceName,endpoint,baseUrl:this.baseUrl},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'C'})}).catch(()=>{});
        // #endregion
        
        console.log('GET:', url);
        return this.http.get<T>(url, {
            observe: 'body',
            responseType: 'json' as any
        });
    }

    post<T>(serviceName: string, endpoint: string, body: any): Observable<T> {
        const url = `${this.baseUrl}/${serviceName}${endpoint}`;
        console.log('POST:', url, body);
        return this.http.post<T>(url, body);
    }

    put<T>(serviceName: string, endpoint: string, body: any): Observable<T> {
        const url = `${this.baseUrl}/${serviceName}${endpoint}`;
        console.log('PUT:', url, body);
        return this.http.put<T>(url, body);
    }

    delete<T>(serviceName: string, endpoint: string): Observable<T> {
        const url = `${this.baseUrl}/${serviceName}${endpoint}`;
        console.log('DELETE:', url);
        return this.http.delete<T>(url);
    }
}
