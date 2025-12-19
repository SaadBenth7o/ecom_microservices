import { Routes } from '@angular/router';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { CustomersComponent } from './features/customers/customers.component';
import { ProductsComponent } from './features/products/products.component';
import { BillsComponent } from './features/bills/bills.component';

export const routes: Routes = [
    { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
    { path: 'dashboard', component: DashboardComponent },
    { path: 'customers', component: CustomersComponent },
    { path: 'products', component: ProductsComponent },
    { path: 'bills', component: BillsComponent },
    { path: '**', redirectTo: '/dashboard' }
];
