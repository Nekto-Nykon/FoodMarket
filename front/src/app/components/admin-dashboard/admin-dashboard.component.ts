import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

interface Category {
  id: number;
  name: string;
  description: string;
}

interface Ingredient {
  id: number;
  name: string;
  description: string;
  unitOfMeasure: string;
  category: Category;
}

interface Supplier {
  id: number;
  companyName: string;
  address: string;
  taxId: string;
  rating: number;
  description: string;
}

interface Review {
  id: number;
  orderId: number;
  buyerId: number;
  buyerEmail: string;
  buyerName: string;
  supplierId: number;
  supplierName: string;
  rating: number;
  comment: string;
  createdAt: string;
}

interface Offer {
  id: number;
  supplier: Supplier;
  ingredient: Ingredient;
  price: number;
  availableQuantity: number;
  minOrderQuantity: number;
  isActive: boolean;
  validFrom: string;
  validUntil: string;
}

interface UserStats {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  isActive: boolean;
  createdAt: string;
  totalOrders?: number;
  totalSpent?: number;
  totalOffers?: number;
  totalSales?: number;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  private apiUrl = 'http://localhost:8080/api';

  // Current user
  currentUser: any = null;

  // Active tab
  activeTab: 'categories' | 'ingredients' | 'offers' | 'suppliers' | 'users' = 'categories';

  // Data
  categories: Category[] = [];
  ingredients: Ingredient[] = [];
  offers: Offer[] = [];
  suppliers: Supplier[] = [];
  users: UserStats[] = [];

  // Forms
  newCategory: Partial<Category> = { name: '', description: '' };
  newIngredient: Partial<Ingredient> = { name: '', description: '', unitOfMeasure: 'кг' };
  selectedCategoryId: number | null = null;

  // Supplier details
  selectedSupplier: Supplier | null = null;
  supplierOffers: Offer[] = [];
  supplierReviews: Review[] = [];

  // Units of measure
  unitsOfMeasure: string[] = ['кг', 'г', 'л', 'мл', 'шт', 'уп', 'ящ'];

  // States
  loading: boolean = false;
  error: string | null = null;
  successMessage: string | null = null;
  showModal: boolean = false;
  modalType: 'category' | 'ingredient' | 'supplier' | null = null;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.currentUser = this.authService.getCurrentUser();
    this.loadCategories();
  }

  // ==================== TAB SWITCHING ====================

  switchTab(tab: 'categories' | 'ingredients' | 'offers' | 'suppliers' | 'users'): void {
    this.activeTab = tab;
    this.error = null;
    this.successMessage = null;

    switch (tab) {
      case 'categories':
        this.loadCategories();
        break;
      case 'ingredients':
        this.loadIngredients();
        this.loadCategories();
        break;
      case 'offers':
        this.loadOffers();
        break;
      case 'suppliers':
        this.loadSuppliers();
        break;
      case 'users':
        this.loadUsers();
        break;
    }
  }

  // ==================== CATEGORIES ====================

  loadCategories(): void {
    this.loading = true;
    this.http.get<Category[]>(`${this.apiUrl}/categories`).subscribe({
      next: (data) => {
        this.categories = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Помилка завантаження категорій';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  createCategory(): void {
    if (!this.newCategory.name?.trim()) {
      this.error = 'Введіть назву категорії';
      return;
    }

    this.http.post<Category>(`${this.apiUrl}/categories`, this.newCategory).subscribe({
      next: (category) => {
        this.categories.push(category);
        this.newCategory = { name: '', description: '' };
        this.successMessage = 'Категорію створено!';
        this.closeModal();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Помилка створення категорії';
        this.cdr.detectChanges();
      }
    });
  }

  deleteCategory(id: number): void {
    if (!confirm('Видалити категорію?')) return;

    this.http.delete(`${this.apiUrl}/categories/${id}`).subscribe({
      next: () => {
        this.categories = this.categories.filter(c => c.id !== id);
        this.successMessage = 'Категорію видалено!';
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Помилка видалення категорії';
        this.cdr.detectChanges();
      }
    });
  }

  // ==================== INGREDIENTS ====================

  loadIngredients(): void {
    this.loading = true;
    this.http.get<Ingredient[]>(`${this.apiUrl}/ingredients`).subscribe({
      next: (data) => {
        this.ingredients = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Помилка завантаження інгредієнтів';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  createIngredient(): void {
    if (!this.newIngredient.name?.trim()) {
      this.error = 'Введіть назву інгредієнта';
      return;
    }

    if (!this.selectedCategoryId) {
      this.error = 'Оберіть категорію';
      return;
    }

    const ingredientData = {
      ...this.newIngredient,
      category: { id: this.selectedCategoryId }
    };

    this.http.post<Ingredient>(`${this.apiUrl}/ingredients`, ingredientData).subscribe({
      next: (ingredient) => {
        this.ingredients.push(ingredient);
        this.newIngredient = { name: '', description: '', unitOfMeasure: 'кг' };
        this.selectedCategoryId = null;
        this.successMessage = 'Інгредієнт створено!';
        this.closeModal();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Помилка створення інгредієнта';
        this.cdr.detectChanges();
      }
    });
  }

  deleteIngredient(id: number): void {
    if (!confirm('Видалити інгредієнт?')) return;

    this.http.delete(`${this.apiUrl}/ingredients/${id}`).subscribe({
      next: () => {
        this.ingredients = this.ingredients.filter(i => i.id !== id);
        this.successMessage = 'Інгредієнт видалено!';
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Помилка видалення інгредієнта';
        this.cdr.detectChanges();
      }
    });
  }

  // ==================== OFFERS ====================

  loadOffers(): void {
    this.loading = true;
    this.http.get<any>(`${this.apiUrl}/offers`).subscribe({
      next: (response) => {
        // Обробка пагінованої відповіді (Page) або масиву
        if (Array.isArray(response)) {
          this.offers = response;
        } else if (response && response.content) {
          this.offers = response.content;
        } else {
          this.offers = [];
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Помилка завантаження пропозицій';
        this.loading = false;
        this.offers = [];
        this.cdr.detectChanges();
      }
    });
  }

  toggleOfferStatus(offer: Offer): void {
    const endpoint = offer.isActive ? 'deactivate' : 'activate';
    this.http.patch<Offer>(`${this.apiUrl}/offers/${offer.id}/${endpoint}`, {}).subscribe({
      next: (updated) => {
        offer.isActive = !offer.isActive;
        this.successMessage = `Пропозицію ${offer.isActive ? 'активовано' : 'деактивовано'}!`;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Помилка зміни статусу';
        this.cdr.detectChanges();
      }
    });
  }

  // ==================== SUPPLIERS ====================

  loadSuppliers(): void {
    this.loading = true;
    this.http.get<any>(`${this.apiUrl}/suppliers`).subscribe({
      next: (response) => {
        // Обробка пагінованої відповіді (Page) або масиву
        if (Array.isArray(response)) {
          this.suppliers = response;
        } else if (response && response.content) {
          this.suppliers = response.content;
        } else {
          this.suppliers = [];
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Помилка завантаження постачальників';
        this.loading = false;
        this.suppliers = [];
        this.cdr.detectChanges();
      }
    });
  }

  viewSupplierDetails(supplier: Supplier): void {
    this.selectedSupplier = supplier;
    this.modalType = 'supplier';
    this.showModal = true;
    this.loadSupplierOffers(supplier.id);
    this.loadSupplierReviews(supplier.id);
  }

  loadSupplierOffers(supplierId: number): void {
    this.http.get<any>(`${this.apiUrl}/offers/supplier/${supplierId}`).subscribe({
      next: (response) => {
        if (Array.isArray(response)) {
          this.supplierOffers = response;
        } else if (response && response.content) {
          this.supplierOffers = response.content;
        } else {
          this.supplierOffers = [];
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading supplier offers:', err);
        this.supplierOffers = [];
        this.cdr.detectChanges();
      }
    });
  }

  loadSupplierReviews(supplierId: number): void {
    this.http.get<Review[]>(`${this.apiUrl}/admin/suppliers/${supplierId}/reviews`).subscribe({
      next: (data) => {
        this.supplierReviews = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading supplier reviews:', err);
        this.supplierReviews = [];
      }
    });
  }

  // ==================== USERS & STATISTICS ====================

  loadUsers(): void {
    this.loading = true;
    this.http.get<UserStats[]>(`${this.apiUrl}/admin/users/statistics`).subscribe({
      next: (data) => {
        this.users = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Помилка завантаження статистики';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  toggleUserStatus(user: UserStats): void {
    const endpoint = user.isActive ? 'deactivate' : 'activate';
    this.http.patch(`${this.apiUrl}/admin/users/${user.id}/${endpoint}`, {}).subscribe({
      next: () => {
        user.isActive = !user.isActive;
        this.successMessage = `Користувача ${user.isActive ? 'активовано' : 'деактивовано'}!`;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Помилка зміни статусу';
        this.cdr.detectChanges();
      }
    });
  }

  // ==================== MODAL ====================

  openModal(type: 'category' | 'ingredient'): void {
    this.modalType = type;
    this.showModal = true;
    this.error = null;
  }

  closeModal(): void {
    this.showModal = false;
    this.modalType = null;
    this.selectedSupplier = null;
    this.supplierOffers = [];
    this.supplierReviews = [];
  }

  // ==================== UTILITY ====================

  logout(): void {
    this.authService.logout();
  }

  getUserInitials(): string {
    if (!this.currentUser?.email) return 'A';
    return this.currentUser.email.charAt(0).toUpperCase();
  }

  clearMessages(): void {
    setTimeout(() => {
      this.successMessage = null;
      this.error = null;
      this.cdr.detectChanges();
    }, 3000);
  }
}
