import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CategoryService } from '../../services/category.service';
import { OfferService } from '../../services/offer.service';
import { CartService, CartItem } from '../../services/cart.service';
import { Category } from '../../models/category.model';
import { Offer, OfferFilters } from '../../models/offer.model';

@Component({
  selector: 'app-buyer-catalog',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './buyer-catalog.component.html',
  styleUrls: ['./buyer-catalog.component.css']
})
export class BuyerCatalogComponent implements OnInit {
  // User info
  currentUser: any = null;

  // Data
  categories: Category[] = [];
  offers: Offer[] = [];
  filteredOffers: Offer[] = [];

  // Filters
  selectedCategoryId: number | null = null;
  searchQuery: string = '';
  sortBy: 'price' | 'rating' = 'price';
  sortOrder: 'asc' | 'desc' = 'asc';

  // States
  loading: boolean = true;
  error: string | null = null;
  successMessage: string | null = null;

  // Cart
  cartCount: number = 0;
  cartTotal: number = 0;

  // Modal for quantity selection
  selectedOffer: Offer | null = null;
  orderQuantity: number = 0;
  showQuantityModal: boolean = false;

  constructor(
    private authService: AuthService,
    private categoryService: CategoryService,
    private offerService: OfferService,
    private cartService: CartService,
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

    // Підписуємось на зміни кошика
    this.cartService.cart$.subscribe(items => {
      this.cartCount = items.length;
      this.cartTotal = this.cartService.getCartTotal();
      this.cdr.detectChanges();
    });

    // Завантажуємо категорії та пропозиції
    this.loadCategories();
    this.loadOffers();
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
   * Load offers
   */
  loadOffers(): void {
    this.loading = true;
    this.error = null;

    this.offerService.getOffers().subscribe({
      next: (data) => {
        this.offers = data;
        this.applyFiltersAndSort();
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
   * Apply filters and sorting
   */
  applyFiltersAndSort(): void {
    let result = [...this.offers];

    // Фільтр за категорією
    if (this.selectedCategoryId) {
      result = result.filter(offer =>
        offer.ingredient?.category?.id === this.selectedCategoryId
      );
    }

    // Фільтр за пошуковим запитом
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase();
      result = result.filter(offer =>
        offer.ingredient?.name?.toLowerCase().includes(query) ||
        offer.supplier?.companyName?.toLowerCase().includes(query)
      );
    }

    // Сортування
    result.sort((a, b) => {
      let compareValue = 0;

      if (this.sortBy === 'price') {
        compareValue = (a.price || 0) - (b.price || 0);
      } else if (this.sortBy === 'rating') {
        const ratingA = a.supplier?.rating || 0;
        const ratingB = b.supplier?.rating || 0;
        compareValue = ratingA - ratingB;
      }

      return this.sortOrder === 'asc' ? compareValue : -compareValue;
    });

    this.filteredOffers = result;
  }

  /**
   * Open quantity modal
   */
  openQuantityModal(offer: Offer): void {
    this.selectedOffer = offer;
    this.orderQuantity = offer.minOrderQuantity;
    this.showQuantityModal = true;
  }

  /**
   * Close quantity modal
   */
  closeQuantityModal(): void {
    this.showQuantityModal = false;
    this.selectedOffer = null;
    this.orderQuantity = 0;
  }

  /**
   * Add to cart
   */
  addToCart(): void {
    if (!this.selectedOffer || this.orderQuantity <= 0) {
      return;
    }

    const success = this.cartService.addToCart(this.selectedOffer, this.orderQuantity);

    if (success) {
      this.successMessage = `${this.selectedOffer.ingredient?.name} додано до кошика!`;
      this.closeQuantityModal();

      // Автоматично приховуємо повідомлення через 3 секунди
      setTimeout(() => {
        this.successMessage = null;
        this.cdr.detectChanges();
      }, 3000);
    } else {
      this.error = 'Не вдалося додати товар до кошика. Перевірте кількість.';
    }

    this.cdr.detectChanges();
  }

  /**
   * Check if offer is in cart
   */
  isInCart(offerId: number): boolean {
    return this.cartService.isInCart(offerId);
  }

  /**
   * Go to cart
   */
  goToCart(): void {
    this.router.navigate(['/cart']);
  }

  /**
   * On category change
   */
  onCategoryChange(): void {
    this.applyFiltersAndSort();
  }

  /**
   * On search
   */
  onSearch(): void {
    this.applyFiltersAndSort();
  }

  /**
   * On sort change
   */
  onSortChange(sortBy: 'price' | 'rating', sortOrder: 'asc' | 'desc'): void {
    this.sortBy = sortBy;
    this.sortOrder = sortOrder;
    this.applyFiltersAndSort();
  }

  /**
   * Reset filters
   */
  resetFilters(): void {
    this.selectedCategoryId = null;
    this.searchQuery = '';
    this.sortBy = 'price';
    this.sortOrder = 'asc';
    this.applyFiltersAndSort();
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
      return 'U';
    }
    return this.currentUser.email.charAt(0).toUpperCase();
  }

  /**
   * Calculate subtotal for modal
   */
  getModalSubtotal(): number {
    if (!this.selectedOffer) return 0;
    return this.orderQuantity * this.selectedOffer.price;
  }
}
