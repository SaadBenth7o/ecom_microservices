import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { HeaderComponent } from '../../shared/header/header.component';
import { BillingService } from '../../core/services/billing.service';
import { CustomerService } from '../../core/services/customer.service';
import { ProductService } from '../../core/services/product.service';
import { Bill, ProductItem } from '../../core/models/bill.model';
import { Customer } from '../../core/models/customer.model';
import { Product } from '../../core/models/product.model';

@Component({
  selector: 'app-bills',
  standalone: true,
  imports: [CommonModule, HeaderComponent, FormsModule, ReactiveFormsModule],
  templateUrl: './bills.component.html',
  styleUrls: ['./bills.component.css']
})
export class BillsComponent implements OnInit {
  bills: Bill[] = [];
  customers: Customer[] = [];
  products: Product[] = [];
  loading = true;
  showForm = false;
  editingBill: Bill | null = null;
  billForm: FormGroup;
  error: string | null = null;

  constructor(
    private billingService: BillingService,
    private customerService: CustomerService,
    private productService: ProductService,
    private fb: FormBuilder
  ) {
    this.billForm = this.fb.group({
      customerId: [null, [Validators.required]],
      billingDate: [new Date().toISOString().split('T')[0], [Validators.required]],
      productItems: this.fb.array([])
    });
  }

  ngOnInit() {
    this.loadCustomers();
    this.loadProducts();
    this.loadBills();
  }

  get productItemsFormArray(): FormArray {
    return this.billForm.get('productItems') as FormArray;
  }

  loadCustomers() {
    this.customerService.getAll().subscribe({
      next: (data) => {
        this.customers = data;
      },
      error: (err) => {
        console.error('Error loading customers:', err);
      }
    });
  }

  loadProducts() {
    this.productService.getAll().subscribe({
      next: (data) => {
        this.products = data;
      },
      error: (err) => {
        console.error('Error loading products:', err);
      }
    });
  }

  loadBills() {
    this.loading = true;
    this.billingService.getAll().subscribe({
      next: (data) => {
        this.bills = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading bills:', err);
        this.error = 'Erreur lors du chargement des factures';
        this.loading = false;
      }
    });
  }

  openCreateForm() {
    this.editingBill = null;
    this.billForm.reset({
      customerId: null,
      billingDate: new Date().toISOString().split('T')[0],
      productItems: []
    });
    this.productItemsFormArray.clear();
    this.addProductItem();
    this.showForm = true;
    this.error = null;
  }

  openEditForm(bill: Bill) {
    this.editingBill = bill;
    this.billForm.patchValue({
      customerId: bill.customerId,
      billingDate: new Date(bill.billingDate).toISOString().split('T')[0]
    });
    this.productItemsFormArray.clear();
    bill.productItems.forEach(item => {
      this.addProductItem(item);
    });
    this.showForm = true;
    this.error = null;
  }

  addProductItem(item?: ProductItem) {
    const itemForm = this.fb.group({
      productId: [item?.productId || '', [Validators.required]],
      quantity: [item?.quantity || 1, [Validators.required, Validators.min(1)]],
      unitPrice: [item?.unitPrice || 0, [Validators.required, Validators.min(0.01)]]
    });
    this.productItemsFormArray.push(itemForm);
  }

  removeProductItem(index: number) {
    this.productItemsFormArray.removeAt(index);
  }

  closeForm() {
    this.showForm = false;
    this.editingBill = null;
    this.billForm.reset();
    this.productItemsFormArray.clear();
    this.error = null;
  }

  saveBill() {
    if (this.billForm.invalid || this.productItemsFormArray.length === 0) {
      this.error = 'Veuillez remplir tous les champs et ajouter au moins un produit';
      return;
    }

    const billData = {
      ...this.billForm.value,
      productItems: this.productItemsFormArray.value
    };
    this.error = null;

    if (this.editingBill) {
      this.billingService.update(this.editingBill.id!, billData).subscribe({
        next: () => {
          this.loadBills();
          this.closeForm();
        },
        error: (err) => {
          this.error = 'Erreur lors de la mise à jour';
        }
      });
    } else {
      this.billingService.create(billData).subscribe({
        next: () => {
          this.loadBills();
          this.closeForm();
        },
        error: (err) => {
          this.error = 'Erreur lors de la création. Vérifiez que le client existe et qu\'il y a au moins un produit.';
        }
      });
    }
  }

  deleteBill(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette facture ?')) {
      this.billingService.delete(id).subscribe({
        next: () => {
          this.loadBills();
        },
        error: (err) => {
          this.error = 'Erreur lors de la suppression';
        }
      });
    }
  }

  calculateTotal(): number {
    return this.productItemsFormArray.value.reduce((total: number, item: any) => {
      return total + (item.quantity * item.unitPrice);
    }, 0);
  }
}
