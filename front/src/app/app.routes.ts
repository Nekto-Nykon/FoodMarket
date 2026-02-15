import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { BuyerCatalogComponent } from './components/buyer-catalog/buyer-catalog.component';
import {SupplierDashboardComponent} from './components/supplier-dashboard/supplier-dashboard.component';
import {OfferEditComponent} from './components/offer-edit/offer-edit.component';
import {CartComponent} from './components/cart/cart.component';
import {AdminDashboardComponent} from './components/admin-dashboard/admin-dashboard.component';
import {MyOrdersComponent} from './components/my-order/my-orders.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent},
  { path: 'buyer', component: BuyerCatalogComponent },
  { path: 'supplier', component: SupplierDashboardComponent },
  { path: 'supplier/offer/:id', component: OfferEditComponent },
  { path: 'cart', component: CartComponent },
  { path: 'admin', component: AdminDashboardComponent },
  { path: 'my-orders', component: MyOrdersComponent },
  { path: '**', redirectTo: '' }
];
