import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CategoryService } from '../../services/category.service';
import { IngredientService } from '../../services/ingredient.service';
import { OfferService } from '../../services/offer.service';
import { Category } from '../../models/category.model';
import { Ingredient } from '../../models/ingredient.model';
import { Offer } from '../../models/offer.model';

@Component({
  selector: 'app-supplier-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './supplier-dashboard.component.html',
  styleUrls: ['./supplier-dashboard.component.css']
})
export class SupplierDashboardComponent implements OnInit {
  // User info
  currentUser: any = null;

  // Data
  categories: Category[] = [];
  ingredients: Ingredient[] = [];
  myOffers: Offer[] = [];

  // States
  loading: boolean = true;
  error: string | null = null;
  successMessage: string | null = null;

  // New Ingredient Form
  newIngredient = {
    name: '',
    description: '',
    categoryId: null as number | null,
    unitOfMeasure: ''
  };
  ingredientSubmitting: boolean = false;

  // New Offer Form
  newOffer = {
    ingredientId: null as number | null,
    price: null as number | null,
    availableQuantity: null as number | null,
    minOrderQuantity: null as number | null,
    validFrom: '',
    validUntil: ''
  };
  offerSubmitting: boolean = false;

  // Units of measure
  unitsOfMeasure = ['кг', 'г', 'л', 'мл', 'шт', 'уп', 'ящ'];

  constructor(
    private authService: AuthService,
    private categoryService: CategoryService,
    private ingredientService: IngredientService,
    private offerService: OfferService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Перевіряємо чи користувач залогінений
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    // Отримуємо дані поточного користувача
    this.currentUser = this.authService.getCurrentUser();

    // Завантажуємо дані
    this.loadCategories();
    this.loadIngredients();
    this.loadMyOffers();
  }

  /**
   * Load categories
   */
  loadCategories(): void {
    this.categoryService.getAllCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading categories:', err);
      }
    });
  }

  /**
   * Load ingredients
   */
  loadIngredients(): void {
    this.ingredientService.getAllIngredients().subscribe({
      next: (data) => {
        this.ingredients = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading ingredients:', err);
      }
    });
  }

  /**
   * Load my offers
   */
  loadMyOffers(): void {
    this.loading = true;
    this.error = null;

    this.offerService.getMyOffers().subscribe({
      next: (data) => {
        this.myOffers = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading offers:', err);
        this.error = 'Не вдалося завантажити пропозиції';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Create new ingredient
   */
  createIngredient(): void {
    if (!this.newIngredient.name || !this.newIngredient.categoryId || !this.newIngredient.unitOfMeasure) {
      return;
    }

    this.ingredientSubmitting = true;
    this.clearMessages();

    const ingredientData = {
      name: this.newIngredient.name,
      description: this.newIngredient.description,
      category: { id: this.newIngredient.categoryId },
      unitOfMeasure: this.newIngredient.unitOfMeasure
    };

    this.ingredientService.createIngredient(ingredientData).subscribe({
      next: (created) => {
        this.successMessage = `Інгредієнт "${created.name}" успішно створено!`;
        this.resetIngredientForm();
        this.loadIngredients();
        this.ingredientSubmitting = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error creating ingredient:', err);
        this.error = 'Помилка при створенні інгредієнта';
        this.ingredientSubmitting = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Create new offer
   */
  createOffer(): void {
    if (!this.newOffer.ingredientId || !this.newOffer.price ||
      !this.newOffer.availableQuantity || !this.newOffer.minOrderQuantity) {
      return;
    }

    this.offerSubmitting = true;
    this.clearMessages();

    const offerData = {
      ingredient: { id: this.newOffer.ingredientId },
      price: this.newOffer.price,
      availableQuantity: this.newOffer.availableQuantity,
      minOrderQuantity: this.newOffer.minOrderQuantity,
      validFrom: this.newOffer.validFrom || null,
      validUntil: this.newOffer.validUntil || null,
      isActive: true
    };

    this.offerService.createOffer(offerData).subscribe({
      next: () => {
        this.successMessage = 'Пропозицію успішно створено!';
        this.resetOfferForm();
        this.loadMyOffers();
        this.offerSubmitting = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error creating offer:', err);
        this.error = 'Помилка при створенні пропозиції';
        this.offerSubmitting = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Deactivate offer
   */
  deactivateOffer(offerId: number): void {
    if (!confirm('Ви впевнені, що хочете деактивувати цю пропозицію?')) {
      return;
    }

    this.offerService.deactivateOffer(offerId).subscribe({
      next: () => {
        this.successMessage = 'Пропозицію деактивовано';
        this.loadMyOffers();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error deactivating offer:', err);
        this.error = 'Помилка при деактивації пропозиції';
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Activate offer
   */
  activateOffer(offerId: number): void {
    this.offerService.activateOffer(offerId).subscribe({
      next: () => {
        this.successMessage = 'Пропозицію активовано';
        this.loadMyOffers();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error activating offer:', err);
        this.error = 'Помилка при активації пропозиції';
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Navigate to edit offer page
   */
  editOffer(offerId: number): void {
    this.router.navigate(['/supplier/offer', offerId]);
  }

  /**
   * Reset ingredient form
   */
  resetIngredientForm(): void {
    this.newIngredient = {
      name: '',
      description: '',
      categoryId: null,
      unitOfMeasure: ''
    };
  }

  /**
   * Reset offer form
   */
  resetOfferForm(): void {
    this.newOffer = {
      ingredientId: null,
      price: null,
      availableQuantity: null,
      minOrderQuantity: null,
      validFrom: '',
      validUntil: ''
    };
  }

  /**
   * Clear messages
   */
  clearMessages(): void {
    this.error = null;
    this.successMessage = null;
  }

  /**
   * Logout
   */
  logout(): void {
    this.authService.logout();
  }

  /**
   * Get user initials for avatar
   */
  getUserInitials(): string {
    if (!this.currentUser || !this.currentUser.email) {
      return 'S';
    }
    return this.currentUser.email.charAt(0).toUpperCase();
  }

  /**
   * Check if offer is expired
   */
  isOfferExpired(offer: Offer): boolean {
    if (!offer.validUntil) return false;
    return new Date(offer.validUntil) < new Date();
  }
}
