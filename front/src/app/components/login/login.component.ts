import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/auth.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;  // ← Видаліть ! (non-null assertion)
  loading: boolean = false;
  error: string | null = null;
  success: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    // ← ІНІЦІАЛІЗУЄМО ФОРМУ В КОНСТРУКТОРІ!
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    // Перевіряємо чи користувач вже залогінений
    if (this.authService.isAuthenticated()) {
      this.redirectByRole();
      return;
    }
    // Форму вже НЕ потрібно ініціалізувати тут!
  }

  /**
   * Submit login form
   */
  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.markFormGroupTouched(this.loginForm);
      return;
    }

    this.loading = true;
    this.error = null;
    this.success = null;

    const credentials: LoginRequest = this.loginForm.value;

    this.authService.login(credentials).subscribe({
      next: (response) => {
        this.loading = false;
        this.success = 'Вхід успішний! Перенаправлення...';

        // Редірект через 1 секунду
        setTimeout(() => {
          this.redirectByRole();
        }, 1000);
      },
      error: (err) => {
        this.loading = false;
        console.error('Login error:', err);

        if (err.status === 401) {
          this.error = 'Невірний email або пароль';
        } else if (err.status === 403) {
          this.error = 'Обліковий запис заблоковано';
        } else if (err.status === 0) {
          this.error = 'Не вдається підключитися до сервера';
        } else {
          this.error = 'Помилка входу. Спробуйте пізніше.';
        }
      }
    });
  }

  /**
   * Redirect user based on role
   */
  private redirectByRole(): void {
    const role = this.authService.getUserRole();

    if (role === 'ROLE_ADMIN' || role === 'ADMIN') {
      this.router.navigate(['/admin']);
    } else if (role === 'ROLE_SUPPLIER' || role === 'SUPPLIER') {
      this.router.navigate(['/supplier']);
    } else if (role === 'BUYER' || role === 'ROLE_BUYER') {
      this.router.navigate(['/buyer']);
    } else {
      this.router.navigate(['/login']);
    }
  }

  /**
   * Mark all form fields as touched to show validation errors
   */
  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  /**
   * Check if field has error
   */
  hasError(fieldName: string, errorType: string): boolean {
    const field = this.loginForm.get(fieldName);
    return !!(field && field.hasError(errorType) && field.touched);
  }
}
