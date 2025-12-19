import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../../shared/header/header.component';
import { BillingService } from '../../core/services/billing.service';
import { Bill } from '../../core/models/bill.model';

@Component({
  selector: 'app-bills',
  standalone: true,
  imports: [CommonModule, HeaderComponent],
  templateUrl: './bills.component.html',
  styleUrls: ['./bills.component.css']
})
export class BillsComponent implements OnInit {
  bills: Bill[] = [];
  loading = true;

  constructor(private billingService: BillingService) { }

  ngOnInit() {
    this.billingService.getAll().subscribe({
      next: (data) => {
        this.bills = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading bills:', err);
        this.loading = false;
      }
    });
  }
}
