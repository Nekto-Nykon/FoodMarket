import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CartService, CartItem } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  currentUser: any = null;
  cartItems: CartItem[] = [];
  cartTotal: number = 0;

  // Checkout
  deliveryAddress: string = '';
  showCheckoutForm: boolean = false;
  submitting: boolean = false;

  // Messages
  error: string | null = null;
  successMessage: string | null = null;

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private orderService: OrderService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.currentUser = this.authService.getCurrentUser();

    // Підписуємось на зміни кошика
    this.cartService.cart$.subscribe(items => {
      this.cartItems = items;
      this.cartTotal = this.cartService.getCartTotal();
      this.cdr.detectChanges();
    });
  }

  /**
   * Update item quantity
   */
  updateQuantity(item: CartItem, newQuantity: number): void {
    if (newQuantity < item.offer.minOrderQuantity) {
      newQuantity = item.offer.minOrderQuantity;
    }
    if (newQuantity > item.offer.availableQuantity) {
      newQuantity = item.offer.availableQuantity;
    }

    this.cartService.updateQuantity(item.offer.id, newQuantity);
  }

  /**
   * Remove item from cart
   */
  removeItem(offerId: number): void {
    this.cartService.removeFromCart(offerId);
  }

  /**
   * Clear entire cart
   */
  clearCart(): void {
    if (confirm('Ви впевнені, що хочете очистити кошик?')) {
      this.cartService.clearCart();
    }
  }

  /**
   * Show checkout form
   */
  proceedToCheckout(): void {
    this.showCheckoutForm = true;
  }

  /**
   * Cancel checkout
   */
  cancelCheckout(): void {
    this.showCheckoutForm = false;
    this.deliveryAddress = '';
  }

  /**
   * Submit order
   */
  submitOrder(): void {
    if (!this.deliveryAddress.trim()) {
      this.error = 'Вкажіть адресу доставки';
      return;
    }

    if (this.cartItems.length === 0) {
      this.error = 'Кошик порожній';
      return;
    }

    this.submitting = true;
    this.error = null;

    this.orderService.createOrder(this.deliveryAddress, this.cartItems).subscribe({
      next: (order) => {
        this.successMessage = `Замовлення #${order.id} успішно створено!`;
        this.cartService.clearCart();
        this.showCheckoutForm = false;
        this.submitting = false;

        // Перенаправляємо на сторінку замовлень через 2 секунди
        setTimeout(() => {
          this.router.navigate(['/my-orders']);
        }, 2000);

        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error creating order:', err);
        this.error = 'Помилка при створенні замовлення. Спробуйте ще раз.';
        this.submitting = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Continue shopping
   */
  continueShopping(): void {
    this.router.navigate(['/buyer']);
  }

  /**
   * Logout
   */
  logout(): void {
    this.authService.logout();
  }

  /**
   * Get user initials
   */
  getUserInitials(): string {
    if (!this.currentUser?.email) return 'U';
    return this.currentUser.email.charAt(0).toUpperCase();
  }
}
