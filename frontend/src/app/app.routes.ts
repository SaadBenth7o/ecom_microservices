import { Routes } from '@angular/router';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { CustomersComponent } from './features/customers/customers.component';
import { ProductsComponent } from './features/products/products.component';
import { BillsComponent } from './features/bills/bills.component';
import { KafkaDashboardComponent } from './features/kafka-dashboard/kafka-dashboard.component';
import { LoginComponent } from './features/login/login.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
    { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
    { path: 'customers', component: CustomersComponent, canActivate: [authGuard] },
    { path: 'products', component: ProductsComponent, canActivate: [authGuard] },
    { path: 'bills', component: BillsComponent, canActivate: [authGuard] },
    { path: 'kafka', component: KafkaDashboardComponent, canActivate: [authGuard] },
    { path: '**', redirectTo: '/dashboard' }
];
