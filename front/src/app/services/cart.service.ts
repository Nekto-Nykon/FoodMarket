import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Offer } from '../models/offer.model';

export interface CartItem {
  offer: Offer;
  quantity: number;
  subtotal: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartItems: CartItem[] = [];
  private cartSubject = new BehaviorSubject<CartItem[]>([]);

  public cart$ = this.cartSubject.asObservable();

  constructor() {
    // Завантажуємо кошик з localStorage при старті
    this.loadCartFromStorage();
  }

  /**
   * Додати товар до кошика
   */
  addToCart(offer: Offer, quantity: number): boolean {
    // Перевірка мінімальної кількості
    if (quantity < offer.minOrderQuantity) {
      return false;
    }

    // Перевірка доступної кількості
    if (quantity > offer.availableQuantity) {
      return false;
    }

    // Перевіряємо чи товар вже в кошику
    const existingItem = this.cartItems.find(item => item.offer.id === offer.id);

    if (existingItem) {
      // Оновлюємо кількість
      const newQuantity = existingItem.quantity + quantity;
      if (newQuantity > offer.availableQuantity) {
        return false;
      }
      existingItem.quantity = newQuantity;
      existingItem.subtotal = newQuantity * offer.price;
    } else {
      // Додаємо новий товар
      this.cartItems.push({
        offer: offer,
        quantity: quantity,
        subtotal: quantity * offer.price
      });
    }

    this.updateCart();
    return true;
  }

  /**
   * Оновити кількість товару в кошику
   */
  updateQuantity(offerId: number, quantity: number): boolean {
    const item = this.cartItems.find(item => item.offer.id === offerId);

    if (!item) return false;

    if (quantity < item.offer.minOrderQuantity || quantity > item.offer.availableQuantity) {
      return false;
    }

    item.quantity = quantity;
    item.subtotal = quantity * item.offer.price;
    this.updateCart();
    return true;
  }

  /**
   * Видалити товар з кошика
   */
  removeFromCart(offerId: number): void {
    this.cartItems = this.cartItems.filter(item => item.offer.id !== offerId);
    this.updateCart();
  }

  /**
   * Очистити кошик
   */
  clearCart(): void {
    this.cartItems = [];
    this.updateCart();
  }

  /**
   * Отримати всі товари в кошику
   */
  getCartItems(): CartItem[] {
    return [...this.cartItems];
  }

  /**
   * Отримати кількість товарів в кошику
   */
  getCartCount(): number {
    return this.cartItems.length;
  }

  /**
   * Отримати загальну суму кошика
   */
  getCartTotal(): number {
    return this.cartItems.reduce((total, item) => total + item.subtotal, 0);
  }

  /**
   * Перевірити чи товар в кошику
   */
  isInCart(offerId: number): boolean {
    return this.cartItems.some(item => item.offer.id === offerId);
  }

  /**
   * Отримати кількість конкретного товару в кошику
   */
  getItemQuantity(offerId: number): number {
    const item = this.cartItems.find(item => item.offer.id === offerId);
    return item ? item.quantity : 0;
  }

  /**
   * Оновити кошик і зберегти в localStorage
   */
  private updateCart(): void {
    this.cartSubject.next([...this.cartItems]);
    this.saveCartToStorage();
  }

  /**
   * Зберегти кошик в localStorage
   */
  private saveCartToStorage(): void {
    localStorage.setItem('cart', JSON.stringify(this.cartItems));
  }

  /**
   * Завантажити кошик з localStorage
   */
  private loadCartFromStorage(): void {
    const cartData = localStorage.getItem('cart');
    if (cartData) {
      try {
        this.cartItems = JSON.parse(cartData);
        this.cartSubject.next([...this.cartItems]);
      } catch {
        this.cartItems = [];
      }
    }
  }
}










































