import { Product } from './product.model';

export interface Bill {
    id?: number;
    billingDate: string | Date;
    customerId: number;
    productItems: ProductItem[];
    totalAmount?: number;
}

export interface ProductItem {
    id?: number;
    productId: number;
    quantity: number;
    unitPrice: number;
    product?: Product;
}
