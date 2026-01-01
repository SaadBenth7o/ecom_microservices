import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Customer } from '../models/customer.model';

@Injectable({
    providedIn: 'root'
})
export class CustomerService {
    private serviceName = 'customer-service';

    constructor(private api: ApiService) { }

    getAll(): Observable<Customer[]> {
        return this.api.get<Customer[]>(this.serviceName, '/api/customers');
    }

    getById(id: number): Observable<Customer> {
        return this.api.get<Customer>(this.serviceName, `/api/customers/${id}`);
    }

    create(customer: Customer): Observable<Customer> {
        return this.api.post<Customer>(this.serviceName, '/api/customers', customer);
    }

    update(id: number, customer: Customer): Observable<Customer> {
        return this.api.put<Customer>(this.serviceName, `/api/customers/${id}`, customer);
    }

    delete(id: number): Observable<void> {
        return this.api.delete<void>(this.serviceName, `/api/customers/${id}`);
    }
}
