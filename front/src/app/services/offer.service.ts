import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Offer, OfferFilters } from '../models/offer.model';
import {CartItem} from './cart.service';
import {CreateOrderRequest, Order} from './order.service';

@Injectable({
  providedIn: 'root'
})
export class OfferService {
  private apiUrl = 'http://localhost:8080/api/offers';

  constructor(private http: HttpClient) {}

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
   * Get all active offers with filters
   */
  getOffers(filters?: OfferFilters): Observable<Offer[]> {
    let params = new HttpParams();
    params = params.set('isActive', 'true');

    if (filters?.categoryId) {
      params = params.set('categoryId', filters.categoryId.toString());
    }

    if (filters?.searchQuery) {
      params = params.set('search', filters.searchQuery);
    }

    return this.http.get<Offer[]>(this.apiUrl, { params });
  }

  /**
   * Get my offers (for supplier)
   */
  getMyOffers(): Observable<Offer[]> {
    return this.http.get<Offer[]>(`${this.apiUrl}/my-offers`);
  }

  /**
   * Get offer by ID
   */
  getOfferById(id: number): Observable<Offer> {
    return this.http.get<Offer>(`${this.apiUrl}/${id}`);
  }

  /**
   * Create new offer
   */
  createOffer(offer: any): Observable<Offer> {
    return this.http.post<Offer>(this.apiUrl, offer);
  }

  /**
   * Update offer
   */
  updateOffer(id: number, offer: any): Observable<Offer> {
    return this.http.put<Offer>(`${this.apiUrl}/${id}`, offer);
  }

  /**
   * Delete offer
   */
  deleteOffer(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Activate offer
   */
  activateOffer(id: number): Observable<Offer> {
    return this.http.patch<Offer>(`${this.apiUrl}/${id}/activate`, {});
  }

  /**
   * Deactivate offer
   */
  deactivateOffer(id: number): Observable<Offer> {
    return this.http.patch<Offer>(`${this.apiUrl}/${id}/deactivate`, {});
  }

  /**
   * Search offers
   */
  searchOffers(query: string): Observable<Offer[]> {
    return this.http.get<Offer[]>(`${this.apiUrl}/search`, {
      params: { query }
    });
  }
}
