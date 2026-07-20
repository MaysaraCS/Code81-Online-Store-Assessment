import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

const CUSTOMER_ONLY = ['CUSTOMER'];
const ANY_STAFF = ['ADMIN', 'STORE_MANAGER', 'SUPPORT_AGENT'];
const ADMIN_ONLY = ['ADMIN'];

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },

  {
    path: '',
    loadComponent: () => import('./shared/layout/main-layout.component').then(m => m.MainLayoutComponent),
    canActivate: [authGuard],
    children: [
      // Catalog - both customers and staff can browse
      { path: '', loadComponent: () => import('./features/catalog/product-list/product-list.component').then(m => m.ProductListComponent) },
      { path: 'products/:id', loadComponent: () => import('./features/catalog/product-detail/product-detail.component').then(m => m.ProductDetailComponent) },

      // Customer-only
      { path: 'cart', canActivate: [roleGuard(CUSTOMER_ONLY)], loadComponent: () => import('./features/cart/cart.component').then(m => m.CartComponent) },
      { path: 'checkout', canActivate: [roleGuard(CUSTOMER_ONLY)], loadComponent: () => import('./features/checkout/checkout.component').then(m => m.CheckoutComponent) },
      { path: 'addresses', canActivate: [roleGuard(CUSTOMER_ONLY)], loadComponent: () => import('./features/account/addresses/addresses.component').then(m => m.AddressesComponent) },
      { path: 'orders', canActivate: [roleGuard(CUSTOMER_ONLY)], loadComponent: () => import('./features/orders/my-orders/my-orders.component').then(m => m.MyOrdersComponent) },

      // Order detail - customer (own order) or staff (any order); ownership is enforced backend-side
      { path: 'orders/:id', loadComponent: () => import('./features/orders/order-detail/order-detail.component').then(m => m.OrderDetailComponent) },

      // Staff-only (any of the three staff roles)
      { path: 'admin/orders', canActivate: [roleGuard(ANY_STAFF)], loadComponent: () => import('./features/admin/orders/orders.component').then(m => m.AdminOrdersComponent) },
      { path: 'admin/products', canActivate: [roleGuard(ANY_STAFF)], loadComponent: () => import('./features/admin/product-list/product-list.component').then(m => m.AdminProductListComponent) },
      { path: 'admin/products/new', canActivate: [roleGuard(ANY_STAFF)], loadComponent: () => import('./features/admin/product-form/product-form.component').then(m => m.AdminProductFormComponent) },
      { path: 'admin/products/:id/edit', canActivate: [roleGuard(ANY_STAFF)], loadComponent: () => import('./features/admin/product-form/product-form.component').then(m => m.AdminProductFormComponent) },
      { path: 'admin/categories', canActivate: [roleGuard(ANY_STAFF)], loadComponent: () => import('./features/admin/category-list/category-list.component').then(m => m.AdminCategoryListComponent) },
      { path: 'admin/categories/new', canActivate: [roleGuard(ANY_STAFF)], loadComponent: () => import('./features/admin/category-form/category-form.component').then(m => m.AdminCategoryFormComponent) },
      { path: 'admin/categories/:id/edit', canActivate: [roleGuard(ANY_STAFF)], loadComponent: () => import('./features/admin/category-form/category-form.component').then(m => m.AdminCategoryFormComponent) },

      // Admin-only
      { path: 'admin/staff', canActivate: [roleGuard(ADMIN_ONLY)], loadComponent: () => import('./features/admin/staff-list/staff-list.component').then(m => m.AdminStaffListComponent) },
      { path: 'admin/staff/new', canActivate: [roleGuard(ADMIN_ONLY)], loadComponent: () => import('./features/admin/staff-form/staff-form.component').then(m => m.AdminStaffFormComponent) },
      { path: 'admin/staff/:id/edit', canActivate: [roleGuard(ADMIN_ONLY)], loadComponent: () => import('./features/admin/staff-form/staff-form.component').then(m => m.AdminStaffFormComponent) },
      { path: 'admin/activity-log', canActivate: [roleGuard(ADMIN_ONLY)], loadComponent: () => import('./features/admin/activity-log/activity-log.component').then(m => m.AdminActivityLogComponent) },

      // Stripe redirect targets - still behind authGuard (inherited from the parent layout route),
      // since the customer's own browser session is still logged in when Stripe redirects back.
      { path: 'payment/success', loadComponent: () => import('./features/payment/payment-success/payment-success.component').then(m => m.PaymentSuccessComponent) },
      { path: 'payment/cancel', loadComponent: () => import('./features/payment/payment-cancel/payment-cancel.component').then(m => m.PaymentCancelComponent) }
    ]
  },

  { path: '**', redirectTo: '' }
];
