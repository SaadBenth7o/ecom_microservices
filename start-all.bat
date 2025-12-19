@echo off
chcp 65001 >nul
color 0A

echo ========================================
echo  Lancement de l'APPLICATION E-COMMERCE
echo ========================================
echo.
echo.

echo Ce script va lancer tous les services:
echo   - Config Service (port 8888)
echo   - Discovery Service (port 8761)
echo   - Gateway Service (port 8888)
echo   - Customer Service (port 8081)
echo   - Inventory Service (port 8082)
echo   - Billing Service (port 8083)
echo   - Frontend Angular 18 (port dynamique)
echo.
echo.

REM Lancement du Config Service
echo Lancement du Config Service...
start "Config Service" cmd /k "cd config-service && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

REM Lancement du Discovery Service (Eureka)
echo Lancement du Discovery Service...
start "Discovery Service" cmd /k "cd discovery-service && mvn spring-boot:run"
timeout /t 20 /nobreak >nul

REM Lancement du Gateway Service
echo Lancement du Gateway Service...
start "Gateway Service" cmd /k "cd gateway-service && mvn spring-boot:run"
timeout /t 20 /nobreak >nul

REM Lancement du Customer Service
echo Lancement du Customer Service...
start "Customer Service" cmd /k "cd customer-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Lancement de l'Inventory Service
echo Lancement de l'Inventory Service...
start "Inventory Service" cmd /k "cd inventory-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Lancement du Billing Service
echo Lancement du Billing Service...
start "Billing Service" cmd /k "cd billing-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Lancement du Frontend Angular
echo Lancement du Frontend Angular 18...
start "Frontend Angular 18" cmd /k "cd frontend && npm start"

echo.
echo ========================================
echo  Tous les services sont en cours de lancement!
echo ========================================
echo.
echo URLs importantes:
echo   - Frontend:          Vérifiez la fenêtre "Frontend Angular 18" pour le port
echo   - Eureka Dashboard:  http://localhost:8761
echo   - Gateway (HTTP):    http://localhost:8888
echo.
echo Note: Le frontend Angular utilise un port dynamique.
echo       Consultez la fenêtre "Frontend Angular 18" pour voir l'URL exacte.
echo.
pause
