import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from './api.service';
import { Customer, PagedResponse } from '../models/customer.model';

@Injectable({
    providedIn: 'root'
})
export class CustomerService {
    private serviceName = 'CUSTOMER-SERVICE';

    constructor(private api: ApiService) { }

    getAll(): Observable<Customer[]> {
        return this.api.get<PagedResponse<Customer>>(this.serviceName, '/api/customers').pipe(
            map(response => response._embedded?.customers || [])
        );
    }

    getById(id: number): Observable<Customer> {
        return this.api.get<Customer>(this.serviceName, `/api/customers/${id}`);
    }

    create(customer: Partial<Customer>): Observable<Customer> {
        return this.api.post<Customer>(this.serviceName, '/api/customers', customer);
    }
}
