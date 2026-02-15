import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

interface OrderItem {
  id: number;
  offerId: number;
  ingredientName: string;
  quantity: number;
  pricePerUnit: number;
  subtotal: number;
}

interface Order {
  id: number;
  status: string;
  deliveryAddress: string;
  totalAmount: number;
  createdAt: string;
  updatedAt: string;
  orderItems: OrderItem[];
}

interface Review {
  id: number;
  orderId: number;
  supplierId: number;
  rating: number;
  comment: string;
}

interface SupplierInfo {
  id: number;
  companyName: string;
}

@Component({
  selector: 'app-my-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './my-orders.component.html',
  styleUrls: ['./my-orders.component.css']
})
export class MyOrdersComponent implements OnInit {
  private apiUrl = 'http://localhost:8080/api';

  orders: Order[] = [];
  loading: boolean = false;
  error: string | null = null;
  successMessage: string | null = null;

  // Modal for order details
  selectedOrder: Order | null = null;
  showOrderModal: boolean = false;

  // Suppliers for selected order
  orderSuppliers: SupplierInfo[] = [];

  // Modal for review
  showReviewModal: boolean = false;
  reviewSupplierId: number | null = null;
  reviewSupplierName: string = '';
  reviewOrderId: number | null = null;
  reviewRating: number = 5;
  reviewComment: string = '';

  // Track which suppliers already have reviews for current order
  reviewedSuppliers: Set<number> = new Set();

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
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.http.get<any>(`${this.apiUrl}/orders/my-orders`).subscribe({
      next: (response) => {
        if (Array.isArray(response)) {
          this.orders = response;
        } else if (response && response.content) {
          this.orders = response.content;
        } else {
          this.orders = [];
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Помилка завантаження замовлень';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // ==================== ORDER DETAILS ====================

  viewOrderDetails(order: Order): void {
    this.selectedOrder = order;
    this.showOrderModal = true;
    this.orderSuppliers = [];
    this.loadOrderSuppliers(order.id);
    this.loadOrderReviews(order.id);
  }

  closeOrderModal(): void {
    this.showOrderModal = false;
    this.selectedOrder = null;
    this.orderSuppliers = [];
    this.reviewedSuppliers.clear();
  }

  loadOrderSuppliers(orderId: number): void {
    this.http.get<SupplierInfo[]>(`${this.apiUrl}/orders/${orderId}/suppliers`).subscribe({
      next: (suppliers) => {
        this.orderSuppliers = suppliers;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading order suppliers:', err);
        this.orderSuppliers = [];
      }
    });
  }

  loadOrderReviews(orderId: number): void {
    // Load existing reviews for this order to know which suppliers already reviewed
    this.http.get<Review[]>(`${this.apiUrl}/reviews/order/${orderId}`).subscribe({
      next: (reviews) => {
        this.reviewedSuppliers.clear();
        reviews.forEach(r => this.reviewedSuppliers.add(r.supplierId));
        this.cdr.detectChanges();
      },
      error: () => {
        // If endpoint doesn't exist or error, just continue
        this.reviewedSuppliers.clear();
      }
    });
  }

  // ==================== ORDER STATUS ====================

  confirmDelivery(order: Order, openReviewAfter: boolean = false): void {
    if (!confirm('Підтвердити отримання замовлення?')) return;

    this.http.patch<Order>(`${this.apiUrl}/orders/${order.id}/confirm-delivery`, {}).subscribe({
      next: (updated) => {
        order.status = 'DELIVERED';
        this.successMessage = 'Доставку підтверджено!';
        this.clearMessageAfterDelay();
        this.cdr.detectChanges();

        // Автоматично відкриваємо вікно для відгуків
        if (openReviewAfter) {
          this.openReviewsForOrder(order);
        }
      },
      error: (err) => {
        this.error = err.error?.message || 'Помилка підтвердження доставки';
        this.clearMessageAfterDelay();
        this.cdr.detectChanges();
      }
    });
  }

  confirmDeliveryAndReview(order: Order): void {
    this.confirmDelivery(order, true);
  }

  openReviewsForOrder(order: Order): void {
    this.selectedOrder = order;
    this.showOrderModal = true;
    this.orderSuppliers = [];
    this.loadOrderSuppliers(order.id);
    this.loadOrderReviews(order.id);
  }

  cancelOrder(order: Order): void {
    if (!confirm('Скасувати замовлення?')) return;

    this.http.patch<Order>(`${this.apiUrl}/orders/${order.id}/cancel`, {}).subscribe({
      next: (updated) => {
        order.status = 'CANCELLED';
        this.successMessage = 'Замовлення скасовано';
        this.clearMessageAfterDelay();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = err.error?.message || 'Помилка скасування замовлення';
        this.clearMessageAfterDelay();
        this.cdr.detectChanges();
      }
    });
  }

  // ==================== REVIEWS ====================

  openReviewModal(orderId: number, supplierId: number, supplierName: string): void {
    this.reviewOrderId = orderId;
    this.reviewSupplierId = supplierId;
    this.reviewSupplierName = supplierName;
    this.reviewRating = 5;
    this.reviewComment = '';
    this.showReviewModal = true;
  }

  closeReviewModal(): void {
    this.showReviewModal = false;
    this.reviewOrderId = null;
    this.reviewSupplierId = null;
    this.reviewSupplierName = '';
    this.reviewRating = 5;
    this.reviewComment = '';
  }

  setRating(rating: number): void {
    this.reviewRating = rating;
  }

  submitReview(): void {
    if (!this.reviewOrderId || !this.reviewSupplierId) return;

    const reviewData = {
      orderId: this.reviewOrderId,
      supplierId: this.reviewSupplierId,
      rating: this.reviewRating,
      comment: this.reviewComment
    };

    this.http.post<Review>(`${this.apiUrl}/reviews`, reviewData).subscribe({
      next: (review) => {
        this.reviewedSuppliers.add(this.reviewSupplierId!);
        this.successMessage = 'Відгук успішно додано!';
        this.closeReviewModal();
        this.clearMessageAfterDelay();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = err.error?.message || 'Помилка додавання відгуку';
        this.clearMessageAfterDelay();
        this.cdr.detectChanges();
      }
    });
  }

  canReview(order: Order, supplierId: number): boolean {
    return order.status === 'DELIVERED' && !this.reviewedSuppliers.has(supplierId);
  }

  hasReviewed(supplierId: number): boolean {
    return this.reviewedSuppliers.has(supplierId);
  }

  // ==================== HELPERS ====================

  getStatusLabel(status: string): string {
    const labels: { [key: string]: string } = {
      'PENDING': 'Очікує',
      'CONFIRMED': 'Підтверджено',
      'PROCESSING': 'Обробляється',
      'SHIPPED': 'Відправлено',
      'DELIVERED': 'Доставлено',
      'CANCELLED': 'Скасовано'
    };
    return labels[status] || status;
  }

  getStatusClass(status: string): string {
    const classes: { [key: string]: string } = {
      'PENDING': 'status-pending',
      'CONFIRMED': 'status-confirmed',
      'PROCESSING': 'status-processing',
      'SHIPPED': 'status-shipped',
      'DELIVERED': 'status-delivered',
      'CANCELLED': 'status-cancelled'
    };
    return classes[status] || '';
  }

  getUniqueSuppliers(): SupplierInfo[] {
    return this.orderSuppliers;
  }

  clearMessageAfterDelay(): void {
    setTimeout(() => {
      this.successMessage = null;
      this.error = null;
      this.cdr.detectChanges();
    }, 4000);
  }

  goToCatalog(): void {
    this.router.navigate(['/catalog']);
  }
}
