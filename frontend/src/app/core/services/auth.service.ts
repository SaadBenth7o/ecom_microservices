import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import Keycloak from 'keycloak-js';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private keycloak: Keycloak;
  private authenticated = new BehaviorSubject<boolean>(false);
  public authenticated$ = this.authenticated.asObservable();

  constructor() {
    this.keycloak = new Keycloak({
      url: 'http://localhost:8080',
      realm: 'microservices',
      clientId: 'angular-client'
    });
  }

  async init(): Promise<boolean> {
    try {
      // #region agent log
      fetch('http://127.0.0.1:7242/ingest/4d728488-9655-4fb7-9ad8-610057eb6692',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'auth.service.ts:21',message:'Keycloak init started',data:{hasHashParams:window.location.hash.includes('state')||window.location.hash.includes('code')||window.location.hash.includes('access_token')},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'A'})}).catch(()=>{});
      // #endregion
      
      // Check if we're coming back from Keycloak login (has hash params)
      const hasHashParams = window.location.hash.includes('state') || 
                            window.location.hash.includes('code') ||
                            window.location.hash.includes('access_token');
      
      const authenticated = await this.keycloak.init({
        onLoad: hasHashParams ? 'login-required' : 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/assets/silent-check-sso.html',
        pkceMethod: 'S256',
        checkLoginIframe: false
      });
      
      // #region agent log
      fetch('http://127.0.0.1:7242/ingest/4d728488-9655-4fb7-9ad8-610057eb6692',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'auth.service.ts:35',message:'Keycloak init completed',data:{authenticated,hasToken:!!this.keycloak.token,tokenLength:this.keycloak.token?.length||0},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'A'})}).catch(()=>{});
      // #endregion
      
      this.authenticated.next(authenticated);
      
      // Clear hash params after successful authentication
      if (authenticated && hasHashParams) {
        // Remove OAuth params from URL
        window.history.replaceState({}, document.title, window.location.pathname);
      }
      
      if (authenticated) {
        // Refresh token periodically
        this.setupTokenRefresh();
      }
      
      return authenticated;
    } catch (error) {
      // #region agent log
      fetch('http://127.0.0.1:7242/ingest/4d728488-9655-4fb7-9ad8-610057eb6692',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'auth.service.ts:50',message:'Keycloak init failed',data:{error:String(error)},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'A'})}).catch(()=>{});
      // #endregion
      
      console.error('Keycloak initialization failed', error);
      return false;
    }
  }

  login(): void {
    this.keycloak.login({
      redirectUri: window.location.origin
    });
  }

  logout(): void {
    this.keycloak.logout({
      redirectUri: window.location.origin
    });
  }

  getToken(): string | undefined {
    const token = this.keycloak.token;
    
    // #region agent log
    fetch('http://127.0.0.1:7242/ingest/4d728488-9655-4fb7-9ad8-610057eb6692',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({location:'auth.service.ts:67',message:'getToken called',data:{hasToken:!!token,tokenLength:token?.length||0,isAuthenticated:this.keycloak.authenticated},timestamp:Date.now(),sessionId:'debug-session',runId:'run1',hypothesisId:'A'})}).catch(()=>{});
    // #endregion
    
    return token;
  }

  isAuthenticated(): boolean {
    return this.keycloak.authenticated || false;
  }

  getUserProfile(): Promise<Keycloak.KeycloakProfile> {
    return this.keycloak.loadUserProfile();
  }

  getUsername(): string | undefined {
    return this.keycloak.tokenParsed?.['preferred_username'] as string | undefined;
  }

  hasRole(role: string): boolean {
    return this.keycloak.hasRealmRole(role);
  }

  private setupTokenRefresh(): void {
    // Refresh token 5 minutes before it expires
    setInterval(() => {
      this.keycloak.updateToken(70)
        .then((refreshed) => {
          if (refreshed) {
            console.log('Token refreshed');
          }
        })
        .catch(() => {
          console.error('Failed to refresh token');
          this.logout();
        });
    }, 60000); // Check every minute
  }
}

