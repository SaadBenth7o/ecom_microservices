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
    this.error = null;
    
    // #region agent log
    fetch('http://127.0.0.1:7242/ingest/4d728488-9655-4fb7-9ad8-610057eb6692',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'customers.component.ts:37',message:'loadCustomers called',data:{},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'F'})}).catch(()=>{});
    // #endregion
    
    console.log('🔄 Loading customers...');
    this.customerService.getAll().subscribe({
      next: (data) => {
        // #region agent log
        fetch('http://127.0.0.1:7242/ingest/4d728488-9655-4fb7-9ad8-610057eb6692',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'customers.component.ts:42',message:'Customers loaded successfully',data:{count:data?.length||0,isEmpty:!data||data.length===0},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'F'})}).catch(()=>{});
        // #endregion
        
        console.log('✅ Customers loaded:', data);
        this.customers = data || [];
        this.loading = false;
        if (this.customers.length === 0) {
          console.warn('⚠️ No customers found - database may be empty');
        }
      },
      error: (err) => {
        // #region agent log
        fetch('http://127.0.0.1:7242/ingest/4d728488-9655-4fb7-9ad8-610057eb6692',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'customers.component.ts:52',message:'Error loading customers',data:{status:err.status,statusText:err.statusText,message:err.message,error:err.error},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:err.status===401?'B':err.status===404?'C':err.status===0?'D':'E'})}).catch(()=>{});
        // #endregion
        
        console.error('❌ Error loading customers:', err);
        console.error('📊 Status:', err.status);
        this.error = `Erreur lors du chargement des clients (${err.status || 'Network Error'})`;
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
