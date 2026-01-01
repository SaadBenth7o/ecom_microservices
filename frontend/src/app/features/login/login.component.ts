import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="login-container">
      <div class="login-card">
        <h1>E-Commerce Admin</h1>
        <p>Veuillez vous connecter pour accéder à l'application</p>
        <button (click)="login()" class="login-button">
          Se connecter avec Keycloak
        </button>
      </div>
    </div>
  `,
  styles: [`
    .login-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    .login-card {
      background: white;
      padding: 2rem;
      border-radius: 8px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      text-align: center;
      max-width: 400px;
    }
    h1 {
      color: #333;
      margin-bottom: 0.5rem;
    }
    p {
      color: #666;
      margin-bottom: 2rem;
    }
    .login-button {
      background: #667eea;
      color: white;
      border: none;
      padding: 12px 24px;
      border-radius: 4px;
      font-size: 16px;
      cursor: pointer;
      width: 100%;
      transition: background 0.3s;
    }
    .login-button:hover {
      background: #5568d3;
    }
  `]
})
export class LoginComponent implements OnInit {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  async ngOnInit() {
    // Check if we're coming back from Keycloak (has hash params)
    const hasHashParams = window.location.hash.includes('state') || 
                          window.location.hash.includes('code') ||
                          window.location.hash.includes('access_token');
    
    if (hasHashParams) {
      // Wait for Keycloak to process the callback
      await new Promise(resolve => setTimeout(resolve, 500));
    }
    
    // If already authenticated, redirect to dashboard
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  login() {
    this.authService.login();
  }
}

