# Online Store - Frontend

Angular 19 frontend for the Online Store API. Standalone components throughout (no NgModules), Reactive Forms, plain services + Angular signals for state (no NgRx).

> **Status:** Phase F1 complete - project scaffold, JWT auth (login/register), route guards, and a placeholder home page proving the login flow works end to end. Catalog, cart, orders, staff, and payment screens are next.

## Running it

```bash
npm install
ng serve
```
Open `http://localhost:4200`. The backend must be running at `http://localhost:8080` (see `src/environments/environment.ts` for the API URL - change it there if your backend runs elsewhere).

## Structure

```
src/app/
  core/
    models/        - TypeScript interfaces mirroring the backend's DTOs field-for-field
    services/       - one service per backend controller (AuthService <-> AuthController, etc.)
    interceptors/   - attaches the JWT to outgoing requests, retries once via refresh on 401
    guards/         - authGuard (must be logged in), roleGuard (must have a specific role)
  features/
    auth/
      login/        - toggles between customer and staff login
      register/     - customer self-registration
    home/           - temporary landing page, replaced once the catalog exists
```

## Design decisions

- **`inject()` instead of constructor injection** for anything used in a field initializer (like a Reactive Form built from `FormBuilder`) - Angular runs field initializers *before* the constructor body, so `private fb: FormBuilder` via a constructor parameter isn't actually assigned yet at that point. `inject()` resolves immediately, avoiding the ordering bug.
- **Tokens in `localStorage`**, not just in memory - keeps someone logged in across a page refresh, at the (documented, accepted) cost of the token being readable by any JS that runs on the page. A stricter setup would use an httpOnly cookie for the refresh token; not worth the extra complexity here.
- **One `LoginComponent` with a Customer/Staff toggle**, not two near-identical components, since the only real difference is which `AuthService` method (and therefore which backend endpoint) the form submits to.
- **Global CSS for shared primitives** (`.btn`, `.field`, `.alert`, `.card`, `.badge` in `src/styles.css`), component-scoped CSS only for page-specific layout - avoids re-defining the same button styles in every component file.
- **System font stack**, no external font dependency - one less thing that can fail to load, and this is a utility app where restraint reads as more professional than a decorative typeface.

## Testing

```bash
ng test
```
