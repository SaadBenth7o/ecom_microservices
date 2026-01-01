import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Check if we're in the middle of a Keycloak callback
  const isKeycloakCallback = window.location.hash.includes('state') || 
                             window.location.hash.includes('code') ||
                             window.location.hash.includes('access_token');

  if (isKeycloakCallback) {
    // Wait for Keycloak to process the callback
    return new Promise((resolve) => {
      setTimeout(() => {
        if (authService.isAuthenticated()) {
          resolve(true);
        } else {
          authService.login();
          resolve(false);
        }
      }, 500);
    });
  }

  if (authService.isAuthenticated()) {
    return true;
  }

  // Redirect to login
  authService.login();
  return false;
};

