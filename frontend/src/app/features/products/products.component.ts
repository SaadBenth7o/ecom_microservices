import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { HeaderComponent } from '../../shared/header/header.component';
import { ProductService } from '../../core/services/product.service';
import { CustomerService } from '../../core/services/customer.service';
import { Product } from '../../core/models/product.model';
import { Customer } from '../../core/models/customer.model';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, HeaderComponent, FormsModule, ReactiveFormsModule],
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent implements OnInit {
  products: Product[] = [];
  customers: Customer[] = [];
  loading = true;
  showForm = false;
  editingProduct: Product | null = null;
  productForm: FormGroup;
  error: string | null = null;

  constructor(
    private productService: ProductService,
    private customerService: CustomerService,
    private fb: FormBuilder
  ) {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(200)]],
      price: [0, [Validators.required, Validators.min(0.01)]],
      quantity: [0, [Validators.required, Validators.min(0)]],
      customerId: [null, [Validators.required]]
    });
  }

  ngOnInit() {
    this.loadCustomers();
    this.loadProducts();
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
    this.loading = true;
    this.productService.getAll().subscribe({
      next: (data) => {
        this.products = data;
        // Enrichir avec les noms des clients
        this.products.forEach(product => {
          const customer = this.customers.find(c => c.id === product.customerId);
          if (customer) {
            product.customerName = customer.name;
          }
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading products:', err);
        this.error = 'Erreur lors du chargement des produits';
        this.loading = false;
      }
    });
  }

  openCreateForm() {
    this.editingProduct = null;
    this.productForm.reset();
    this.showForm = true;
    this.error = null;
  }

  openEditForm(product: Product) {
    this.editingProduct = product;
    this.productForm.patchValue({
      name: product.name,
      price: product.price,
      quantity: product.quantity,
      customerId: product.customerId
    });
    this.showForm = true;
    this.error = null;
  }

  closeForm() {
    this.showForm = false;
    this.editingProduct = null;
    this.productForm.reset();
    this.error = null;
  }

  saveProduct() {
    if (this.productForm.invalid) {
      this.error = 'Veuillez remplir tous les champs correctement';
      return;
    }

    const productData = this.productForm.value;
    this.error = null;

    if (this.editingProduct) {
      this.productService.update(this.editingProduct.id!, productData).subscribe({
        next: () => {
          this.loadProducts();
          this.closeForm();
        },
        error: (err) => {
          this.error = 'Erreur lors de la mise à jour';
        }
      });
    } else {
      this.productService.create(productData).subscribe({
        next: () => {
          this.loadProducts();
          this.closeForm();
        },
        error: (err) => {
          this.error = 'Erreur lors de la création. Vérifiez que le client existe.';
        }
      });
    }
  }

  deleteProduct(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
      this.productService.delete(id).subscribe({
        next: () => {
          this.loadProducts();
        },
        error: (err) => {
          this.error = 'Erreur lors de la suppression';
        }
      });
    }
  }

  getFieldError(fieldName: string): string {
    const field = this.productForm.get(fieldName);
    if (field?.hasError('required')) {
      return `${fieldName} est obligatoire`;
    }
    if (field?.hasError('min')) {
      return `${fieldName} doit être supérieur à ${field.errors?.['min'].min}`;
    }
    if (field?.hasError('minlength')) {
      return `${fieldName} doit contenir au moins ${field.errors?.['minlength'].requiredLength} caractères`;
    }
    return '';
  }
}
