import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { SidebarComponent } from './shared/sidebar/sidebar.component';
import { AuthService } from './core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, CommonModule],
  template: `
    <div class="app-layout" *ngIf="isAuthenticated">
      <app-sidebar></app-sidebar>
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
    </div>
    <router-outlet *ngIf="!isAuthenticated"></router-outlet>
  `,
  styles: [`
    .app-layout {
      display: flex;
      min-height: 100vh;
    }
    
    .main-content {
      margin-left: 260px;
      flex: 1;
      background: #f9fafb;
      min-height: 100vh;
    }
  `]
})
export class AppComponent implements OnInit {
  title = 'E-Commerce Admin';
  isAuthenticated = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  async ngOnInit() {
    const authenticated = await this.authService.init();
    this.isAuthenticated = authenticated;
    
    // Wait a bit for Keycloak to fully process the callback
    if (authenticated) {
      // Small delay to ensure Keycloak has processed everything
      setTimeout(() => {
        this.isAuthenticated = this.authService.isAuthenticated();
        if (this.isAuthenticated && this.router.url === '/login') {
          this.router.navigate(['/dashboard']);
        }
      }, 100);
    }
    
    this.authService.authenticated$.subscribe(auth => {
      this.isAuthenticated = auth;
      if (!auth && this.router.url !== '/login' && !window.location.hash.includes('state')) {
        this.router.navigate(['/login']);
      } else if (auth && this.router.url === '/login') {
        this.router.navigate(['/dashboard']);
      }
    });
  }
}
