import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CartItem } from './cart.service';

export interface CreateOrderRequest {
  deliveryAddress: string;
  items: OrderItemRequest[];
}

export interface OrderItemRequest {
  offerId: number;
  quantity: number;
}

export interface Order {
  id: number;
  buyerId: number;
  buyerEmail: string;
  totalAmount: number;
  status: string;
  deliveryAddress: string;
  createdAt: string;
  updatedAt: string;
  orderItems: OrderItem[];
}

export interface OrderItem {
  id: number;
  orderId: number;
  offer: any;
  quantity: number;
  pricePerUnit: number;
  subtotal: number;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8080/api/orders';

  constructor(private http: HttpClient) {}

  /**
   * Створити замовлення з кошика
   */
  createOrder(deliveryAddress: string, cartItems: CartItem[]): Observable<Order> {
    const request: CreateOrderRequest = {
      deliveryAddress: deliveryAddress,
      items: cartItems.map(item => ({
        offerId: item.offer.id,
        quantity: item.quantity
      }))
    };

    return this.http.post<Order>(this.apiUrl, request);
  }

  /**
   * Отримати мої замовлення (для buyer)
   */
  getMyOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/my-orders`);
  }

  /**
   * Отримати замовлення по ID
   */
  getOrderById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${id}`);
  }

  /**
   * Скасувати замовлення
   */
  cancelOrder(id: number): Observable<Order> {
    return this.http.patch<Order>(`${this.apiUrl}/${id}/cancel`, {});
  }

  /**
   * Отримати замовлення для постачальника
   */
  getSupplierOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/supplier-orders`);
  }

  /**
   * Оновити статус замовлення (для постачальника)
   */
  updateOrderStatus(id: number, status: string): Observable<Order> {
    return this.http.patch<Order>(`${this.apiUrl}/${id}/status`, { status });
  }
}










































