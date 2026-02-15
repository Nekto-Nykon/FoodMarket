import { Component, OnInit, ChangeDetectorRef  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CategoryService } from '../../services/category.service';
import { Category } from '../../models/category.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  categories: Category[] = [];
  loading: boolean = true;
  error: string | null = null;

  constructor(
    private categoryService: CategoryService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }
  onLoginClick() {
    console.log('🔵 Login link clicked!');
    console.log('Current URL after LoginClick:', window.location.href);
  }
  onRegisterClick(){
    console.log('🔵 Register link clicked!');
    console.log('Current URL after RegisterClick:', window.location.href);
  }
  /**
   * Load all categories from backend
   */
  loadCategories(): void {
    console.log('🔄 Starting to load categories...');
    this.loading = true;
    this.error = null;

    this.categoryService.getAllCategories().subscribe({
      next: (data) => {
        console.log('✅ Categories loaded:', data);
        this.categories = data;
        this.loading = false;
        this.cdr.detectChanges();
        console.log('Loading state:', this.loading);
      },
      error: (err) => {
        console.error('❌ Error loading categories:', err);
        this.error = 'Не вдалося завантажити категорії. Перевірте з\'єднання з сервером.';
        this.loading = false;
        this.cdr.detectChanges();
      },
      complete: () => {
        console.log('🏁 Loading complete');
      }
    });
  }
}

























































































































































