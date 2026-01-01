import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Bill } from '../models/bill.model';

@Injectable({
    providedIn: 'root'
})
export class BillingService {
    private serviceName = 'BILLING-SERVICE';

    constructor(private api: ApiService) { }

    getAll(): Observable<Bill[]> {
        return this.api.get<Bill[]>(this.serviceName, '/api/bills');
    }

    getById(id: number): Observable<Bill> {
        return this.api.get<Bill>(this.serviceName, `/api/bills/${id}`);
    }

    getByCustomer(customerId: number): Observable<Bill[]> {
        return this.api.get<Bill[]>(this.serviceName, `/api/bills/customer/${customerId}`);
    }

    create(bill: Bill): Observable<Bill> {
        return this.api.post<Bill>(this.serviceName, '/api/bills', bill);
    }

    update(id: number, bill: Bill): Observable<Bill> {
        return this.api.put<Bill>(this.serviceName, `/api/bills/${id}`, bill);
    }

    delete(id: number): Observable<void> {
        return this.api.delete<void>(this.serviceName, `/api/bills/${id}`);
    }
}
