import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from './api.service';
import { Bill } from '../models/bill.model';
import { PagedResponse } from '../models/customer.model';

@Injectable({
    providedIn: 'root'
})
export class BillingService {
    private serviceName = 'BILLING-SERVICE';

    constructor(private api: ApiService) { }

    getAll(): Observable<Bill[]> {
        return this.api.get<PagedResponse<Bill>>(this.serviceName, '/api/bills').pipe(
            map(response => response._embedded?.bills || [])
        );
    }

    getById(id: number): Observable<Bill> {
        return this.api.get<Bill>(this.serviceName, `/api/bills/${id}`);
    }
}
