import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../../shared/header/header.component';
import { CustomerService } from '../../core/services/customer.service';
import { ProductService } from '../../core/services/product.service';
import { BillingService } from '../../core/services/billing.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats = {
    customers: 0,
    products: 0,
    bills: 0
  };
  loading = true;

  constructor(
    private customerService: CustomerService,
    private productService: ProductService,
    private billingService: BillingService
  ) { }

  ngOnInit() {
    forkJoin({
      customers: this.customerService.getAll(),
      products: this.productService.getAll(),
      bills: this.billingService.getAll()
    }).subscribe({
      next: (data) => {
        this.stats.customers = data.customers.length;
        this.stats.products = data.products.length;
        this.stats.bills = data.bills.length;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading dashboard stats:', err);
        this.loading = false;
      }
    });
  }
}
