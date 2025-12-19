export interface Bill {
    id: number;
    billingDate: string;
    customerId: number;
    _links?: {
        self: { href: string };
        bill: { href: string };
    };
}
