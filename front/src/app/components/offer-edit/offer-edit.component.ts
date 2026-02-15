import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { OfferService } from '../../services/offer.service';
import { Offer } from '../../models/offer.model';

@Component({
  selector: 'app-offer-edit',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './offer-edit.component.html',
  styleUrls: ['./offer-edit.component.css']
})
export class OfferEditComponent implements OnInit {
  // User info
  currentUser: any = null;

  // Offer data
  offerId: number | null = null;
  offer: Offer | null = null;

  // Form data
  editForm = {
    price: null as number | null,
    availableQuantity: null as number | null,
    minOrderQuantity: null as number | null,
    validFrom: '',
    validUntil: '',
    isActive: true
  };

  // States
  loading: boolean = true;
  submitting: boolean = false;
  error: string | null = null;
  successMessage: string | null = null;

  constructor(
    private authService: AuthService,
    private offerService: OfferService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Перевіряємо чи користувач залогінений
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    this.currentUser = this.authService.getCurrentUser();

    // Отримуємо ID offer з URL
    this.route.params.subscribe(params => {
      this.offerId = +params['id'];
      if (this.offerId) {
        this.loadOffer();
      }
    });
  }

  /**
   * Load offer data
   */
  loadOffer(): void {
    if (!this.offerId) return;

    this.loading = true;
    this.offerService.getOfferById(this.offerId).subscribe({
      next: (data) => {
        this.offer = data;
        this.populateForm();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading offer:', err);
        this.error = 'Не вдалося завантажити пропозицію';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Populate form with offer data
   */
  populateForm(): void {
    if (!this.offer) return;

    this.editForm = {
      price: this.offer.price,
      availableQuantity: this.offer.availableQuantity,
      minOrderQuantity: this.offer.minOrderQuantity,
      validFrom: this.offer.validFrom ? this.formatDateForInput(this.offer.validFrom) : '',
      validUntil: this.offer.validUntil ? this.formatDateForInput(this.offer.validUntil) : '',
      isActive: this.offer.isActive
    };
  }

  /**
   * Format date for input field
   */
  formatDateForInput(dateStr: string): string {
    const date = new Date(dateStr);
    return date.toISOString().split('T')[0];
  }

  /**
   * Save changes
   */
  saveChanges(): void {
    if (!this.offerId || !this.editForm.price || !this.editForm.availableQuantity || !this.editForm.minOrderQuantity) {
      return;
    }

    this.submitting = true;
    this.clearMessages();

    const updateData = {
      price: this.editForm.price,
      availableQuantity: this.editForm.availableQuantity,
      minOrderQuantity: this.editForm.minOrderQuantity,
      validFrom: this.editForm.validFrom || null,
      validUntil: this.editForm.validUntil || null,
      isActive: this.editForm.isActive
    };

    this.offerService.updateOffer(this.offerId, updateData).subscribe({
      next: () => {
        this.successMessage = 'Пропозицію успішно оновлено!';
        this.submitting = false;
        this.cdr.detectChanges();

        // Повертаємось на сторінку постачальника через 1.5 сек
        setTimeout(() => {
          this.router.navigate(['/supplier']);
        }, 1500);
      },
      error: (err) => {
        console.error('Error updating offer:', err);
        this.error = 'Помилка при оновленні пропозиції';
        this.submitting = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Clear messages
   */
  clearMessages(): void {
    this.error = null;
    this.successMessage = null;
  }

  /**
   * Go back to supplier dashboard
   */
  goBack(): void {
    this.router.navigate(['/supplier']);
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
}
