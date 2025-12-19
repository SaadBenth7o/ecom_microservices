export interface Customer {
    id: number;
    name: string;
    email: string;
    _links?: {
        self: { href: string };
        customer: { href: string; templated?: boolean };
    };
}

export interface PagedResponse<T> {
    _embedded?: {
        customers?: T[];
        products?: T[];
        bills?: T[];
    };
    _links: {
        self: { href: string };
        profile?: { href: string };
    };
    page: {
        size: number;
        totalElements: number;
        totalPages: number;
        number: number;
    };
}
