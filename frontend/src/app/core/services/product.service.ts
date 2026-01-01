import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Product } from '../models/product.model';

@Injectable({
    providedIn: 'root'
})
export class ProductService {
    private serviceName = 'INVENTORY-SERVICE';

    constructor(private api: ApiService) { }

    getAll(): Observable<Product[]> {
        return this.api.get<Product[]>(this.serviceName, '/api/products');
    }

    getById(id: number): Observable<Product> {
        return this.api.get<Product>(this.serviceName, `/api/products/${id}`);
    }

    getByCustomer(customerId: number): Observable<Product[]> {
        return this.api.get<Product[]>(this.serviceName, `/api/products/customer/${customerId}`);
    }

    create(product: Product): Observable<Product> {
        return this.api.post<Product>(this.serviceName, '/api/products', product);
    }

    update(id: number, product: Product): Observable<Product> {
        return this.api.put<Product>(this.serviceName, `/api/products/${id}`, product);
    }

    delete(id: number): Observable<void> {
        return this.api.delete<void>(this.serviceName, `/api/products/${id}`);
    }
}
