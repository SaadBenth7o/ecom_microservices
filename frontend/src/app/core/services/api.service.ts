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
        console.log('GET:', url);
        return this.http.get<T>(url);
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
