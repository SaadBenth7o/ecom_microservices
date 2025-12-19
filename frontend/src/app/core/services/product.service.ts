import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from './api.service';
import { Product } from '../models/product.model';
import { PagedResponse } from '../models/customer.model';

@Injectable({
    providedIn: 'root'
})
export class ProductService {
    private serviceName = 'INVENTORY-SERVICE';

    constructor(private api: ApiService) { }

    getAll(): Observable<Product[]> {
        return this.api.get<PagedResponse<Product>>(this.serviceName, '/api/products').pipe(
            map(response => response._embedded?.products || [])
        );
    }

    getById(id: string): Observable<Product> {
        return this.api.get<Product>(this.serviceName, `/api/products/${id}`);
    }
}
