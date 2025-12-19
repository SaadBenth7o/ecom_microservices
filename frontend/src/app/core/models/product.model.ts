export interface Product {
    id: string;
    name: string;
    price: number;
    quantity: number;
    _links?: {
        self: { href: string };
        product: { href: string };
    };
}
