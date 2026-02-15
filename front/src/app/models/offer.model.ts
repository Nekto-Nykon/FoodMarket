import { Supplier} from './supplier.model';
import { Ingredient} from './ingredient.model';

export interface Offer {
  id: number;
  supplier: Supplier;
  ingredient: Ingredient;
  price: number;
  availableQuantity: number;
  minOrderQuantity: number;
  isActive: boolean;
  validFrom: string;
  validUntil: string;
  createdAt: string;
  updatedAt: string;
}

export interface OfferFilters {
  categoryId?: number;
  searchQuery?: string;
  sortBy?: 'price' | 'rating';
  sortOrder?: 'asc' | 'desc';
}





























































































































































