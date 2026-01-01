import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HeaderComponent } from '../../shared/header/header.component';
import { CustomerService } from '../../core/services/customer.service';
import { Customer } from '../../core/models/customer.model';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, HeaderComponent, FormsModule, ReactiveFormsModule],
  templateUrl: './customers.component.html',
  styleUrls: ['./customers.component.css']
})
export class CustomersComponent implements OnInit {
  customers: Customer[] = [];
  loading = true;
  showForm = false;
  editingCustomer: Customer | null = null;
  customerForm: FormGroup;
  error: string | null = null;

  constructor(
    private customerService: CustomerService,
    private fb: FormBuilder
  ) {
    this.customerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit() {
    this.loadCustomers();
  }

  loadCustomers() {
    this.loading = true;
    this.customerService.getAll().subscribe({
      next: (data) => {
        this.customers = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading customers:', err);
        this.error = 'Erreur lors du chargement des clients';
        this.loading = false;
      }
    });
  }

  openCreateForm() {
    this.editingCustomer = null;
    this.customerForm.reset();
    this.showForm = true;
    this.error = null;
  }

  openEditForm(customer: Customer) {
    this.editingCustomer = customer;
    this.customerForm.patchValue({
      name: customer.name,
      email: customer.email
    });
    this.showForm = true;
    this.error = null;
  }

  closeForm() {
    this.showForm = false;
    this.editingCustomer = null;
    this.customerForm.reset();
    this.error = null;
  }

  saveCustomer() {
    if (this.customerForm.invalid) {
      this.error = 'Veuillez remplir tous les champs correctement';
      return;
    }

    const customerData = this.customerForm.value;
    this.error = null;

    if (this.editingCustomer) {
      // Update
      this.customerService.update(this.editingCustomer.id!, customerData).subscribe({
        next: () => {
          this.loadCustomers();
          this.closeForm();
        },
        error: (err) => {
          this.error = err.status === 409 ? 'Cet email est déjà utilisé' : 'Erreur lors de la mise à jour';
        }
      });
    } else {
      // Create
      this.customerService.create(customerData).subscribe({
        next: () => {
          this.loadCustomers();
          this.closeForm();
        },
        error: (err) => {
          this.error = err.status === 409 ? 'Cet email est déjà utilisé' : 'Erreur lors de la création';
        }
      });
    }
  }

  deleteCustomer(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce client ?')) {
      this.customerService.delete(id).subscribe({
        next: () => {
          this.loadCustomers();
        },
        error: (err) => {
          this.error = 'Erreur lors de la suppression';
        }
      });
    }
  }

  getFieldError(fieldName: string): string {
    const field = this.customerForm.get(fieldName);
    if (field?.hasError('required')) {
      return `${fieldName} est obligatoire`;
    }
    if (field?.hasError('email')) {
      return 'Email invalide';
    }
    if (field?.hasError('minlength')) {
      return `${fieldName} doit contenir au moins ${field.errors?.['minlength'].requiredLength} caractères`;
    }
    if (field?.hasError('maxlength')) {
      return `${fieldName} ne peut pas dépasser ${field.errors?.['maxlength'].requiredLength} caractères`;
    }
    return '';
  }
}
