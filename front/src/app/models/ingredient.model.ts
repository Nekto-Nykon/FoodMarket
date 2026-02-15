import {Category} from './category.model';

export interface Ingredient {
  id: number;
  name: string;
  description: string;
  unitOfMeasure: string;
  category: Category;
}
