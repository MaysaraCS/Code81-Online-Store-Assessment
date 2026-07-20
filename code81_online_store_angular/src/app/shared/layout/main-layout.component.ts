import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.css'
})
export class MainLayoutComponent {
  protected authService = inject(AuthService);
  private router = inject(Router);

  get isAdmin(): boolean {
    return this.authService.role() === 'ADMIN';
  }

  get isStaff(): boolean {
    return this.authService.isStaff();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
