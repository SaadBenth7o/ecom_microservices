@echo off
chcp 65001 >nul
color 0A

echo ========================================
echo  Lancement de l'APPLICATION E-COMMERCE
echo  Architecture Microservices Unifiee
echo ========================================
echo.
echo.

echo Ce script va lancer tous les services:
echo   Infrastructure:
echo   - Keycloak Auth Service (port 8080)
echo   - Kafka + Zookeeper (Docker)
echo.
echo   Core Services:
echo   - Config Service (port 9999)
echo   - Discovery Service (port 8761)
echo   - Gateway Service (port 8888)
echo.
echo   Business Services:
echo   - Customer Service (port 8081)
echo   - Inventory Service (port 8082)
echo   - Billing Service (port 8083)
echo.
echo   AI and Analytics:
echo   - Chatbot Service (port 8087)
echo   - MCP Server (port 8989)
echo   - Data Analytics Service (port 8090)
echo.
echo   Frontend:
echo   - Frontend Angular 18 (port dynamique)
echo.
echo.

REM Verifier Docker Desktop
echo Verification de Docker...
docker info >nul 2>&1
if errorlevel 1 (
    echo [AVERTISSEMENT] Docker n'est pas demarre. Le service Data Analytics ne fonctionnera pas sans Kafka.
    echo Veuillez demarrer Docker Desktop pour utiliser Kafka.
    pause
) else (
    echo [OK] Docker est disponible.
)
echo.

REM Demarrer Kafka et Zookeeper via Docker Compose
echo Lancement de Kafka et Zookeeper...
start "Kafka-Zookeeper" cmd /k "docker-compose up"
timeout /t 10 /nobreak >nul

REM Lancement du Discovery Service (Eureka) - PREMIER
echo Lancement du Discovery Service...
start "Discovery Service" cmd /k "cd discovery-service && mvn spring-boot:run"
timeout /t 25 /nobreak >nul

REM Lancement du Gateway Service
echo Lancement du Gateway Service...
start "Gateway Service" cmd /k "cd gateway-service && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

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

REM Lancement du MCP Server
echo Lancement du MCP Server...
start "MCP Server" cmd /k "cd chatbot-service\mcp-server && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Lancement du Chatbot Service
echo Lancement du Chatbot Service...
start "Chatbot Service" cmd /k "cd chatbot-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Lancement du Data Analytics Service
echo Lancement du Data Analytics Service...
start "Data Analytics" cmd /k "cd data-analytics-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

REM Lancement du Frontend Angular
echo Lancement du Frontend Angular 18...
start "Frontend Angular 18" cmd /k "cd frontend && npm start"

echo.
echo =========================================================
echo  Tous les services sont en cours de lancement!
echo =========================================================
echo.
echo URLs importantes:
echo.
echo   INFRASTRUCTURE:
echo   - Keycloak Admin:      http://localhost:8080 (admin/123)
echo   - Eureka Dashboard:    http://localhost:8761
echo   - Gateway (HTTP):      http://localhost:8888
echo.
echo   BUSINESS SERVICES (via Gateway):
echo   - Customer Service:    http://localhost:8888/customer-service/api/customers
echo   - Inventory Service:   http://localhost:8888/inventory-service/api/products
echo   - Billing Service:     http://localhost:8888/billing-service/api/bills
echo.
echo   AI / ANALYTICS:
echo   - Chatbot Service:     http://localhost:8087
echo   - Data Analytics:      http://localhost:8090
echo.
echo   FRONTEND:
echo   - Angular App:         Verifiez la fenetre "Frontend Angular 18"
echo.
echo =========================================================
echo  IMPORTANT: Demarrez Keycloak manuellement si necessaire:
echo  cd C:\keycloak-26.4.6\bin && .\kc.bat start-dev
echo =========================================================
echo.
echo Note: N'oubliez pas de configurer les variables d'environnement:
echo   - GEMINI_KEY: Votre cle API Gemini
echo   - TELEGRAM_API_KEY: Votre token de bot Telegram
echo.
pause
