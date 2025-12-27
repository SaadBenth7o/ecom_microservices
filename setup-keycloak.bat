@echo off
chcp 65001 >nul
color 0B

echo =========================================
echo  Configuration Keycloak pour Microservices
echo =========================================
echo.

echo Ce script vous guide pour configurer Keycloak.
echo.
echo ETAPES A SUIVRE:
echo.
echo 1. DEMARRER KEYCLOAK:
echo    cd C:\keycloak-26.4.6\bin
echo    .\kc.bat start-dev
echo.
echo 2. ACCEDER A LA CONSOLE ADMIN:
echo    URL: http://localhost:8080
echo    Username: admin
echo    Password: 123
echo.
echo 3. CREER LE REALM "microservices":
echo    - Dans le menu gauche, cliquez sur le dropdown "master"
echo    - Cliquez "Create Realm"
echo    - Name: microservices
echo    - Cliquez "Create"
echo.
echo 4. CREER LE CLIENT "gateway-client" (pour les services backend):
echo    - Allez dans Clients > Create client
echo    - Client ID: gateway-client
echo    - Client type: OpenID Connect
echo    - Cliquez Next > Next
echo    - Client authentication: ON
echo    - Cliquez Save
echo.
echo 5. CREER LE CLIENT "angular-client" (pour le frontend):
echo    - Allez dans Clients > Create client
echo    - Client ID: angular-client
echo    - Client type: OpenID Connect
echo    - Cliquez Next > Next
echo    - Client authentication: OFF
echo    - Valid redirect URIs: http://localhost:4200/*
echo    - Web origins: http://localhost:4200
echo    - Cliquez Save
echo.
echo 6. CREER UN UTILISATEUR TEST:
echo    - Allez dans Users > Add user
echo    - Username: user1
echo    - Email verified: ON
echo    - Cliquez Create
echo    - Allez dans l'onglet Credentials
echo    - Set password: user123
echo    - Temporary: OFF
echo    - Cliquez Reset password
echo.
echo =========================================
echo  CONFIGURATION TERMINEE!
echo =========================================
echo.
echo Une fois configure, vous pouvez obtenir un token avec:
echo.
echo curl -X POST "http://localhost:8080/realms/microservices/protocol/openid-connect/token" ^
echo   -d "grant_type=password" ^
echo   -d "client_id=angular-client" ^
echo   -d "username=user1" ^
echo   -d "password=user123"
echo.
pause
