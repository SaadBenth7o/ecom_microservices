@echo off
chcp 65001 >nul
color 0A
cls

echo ================================================================
echo   DEMARRAGE DE TOUS LES SERVICES
echo ================================================================
echo.

REM Verification Docker
echo [VERIFICATION] Docker...
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERREUR] Docker n'est pas demarre!
    pause
    exit /b 1
)
echo [OK] Docker est actif
echo.

REM Demarrage Keycloak
echo [0/12] Demarrage Keycloak - Port 8080...
if exist "C:\keycloak-26.4.6\bin\kc.bat" (
    start "Keycloak" cmd /k "title Keycloak - Port 8080 && cd /d C:\keycloak-26.4.6\bin && kc.bat start-dev"
    timeout /t 15 /nobreak >nul
    echo [OK] Keycloak demarre
) else (
    echo [ATTENTION] Keycloak non trouve dans C:\keycloak-26.4.6\bin
    echo            Demarrez Keycloak manuellement si necessaire
)
echo.

REM Demarrage Kafka
echo [1/12] Demarrage Kafka + Zookeeper...
docker stop broker zookeeper >nul 2>&1
docker rm broker zookeeper >nul 2>&1
start "Kafka-Zookeeper" cmd /k "title Kafka-Zookeeper && cd /d %~dp0 && docker-compose up"
timeout /t 8 /nobreak >nul
echo [OK] Kafka demarre
echo.

REM Discovery
echo [2/11] Discovery Service (Eureka) - Port 8761...
start "Discovery-Service" cmd /k "title Discovery Service - Port 8761 && cd /d %~dp0discovery-service && mvn spring-boot:run"
timeout /t 20 /nobreak >nul
echo [OK] Discovery Service demarre
echo.

REM Keycloak Auth Service
echo [3/12] Keycloak Auth Service - Port 8085...
start "Keycloak-Auth-Service" cmd /k "title Keycloak Auth Service - Port 8085 && cd /d %~dp0keycloak-auth-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul
echo [OK] Keycloak Auth Service demarre
echo.

REM Gateway
echo [4/12] Gateway Service - Port 8888...
start "Gateway-Service" cmd /k "title Gateway Service - Port 8888 && cd /d %~dp0gateway-service && mvn spring-boot:run"
timeout /t 12 /nobreak >nul
echo [OK] Gateway Service demarre
echo.

REM Customer
echo [5/12] Customer Service - Port 8081...
start "Customer-Service" cmd /k "title Customer Service - Port 8081 && cd /d %~dp0customer-service && mvn spring-boot:run"
timeout /t 6 /nobreak >nul
echo [OK] Customer Service demarre
echo.

REM Inventory
echo [6/12] Inventory Service - Port 8082...
start "Inventory-Service" cmd /k "title Inventory Service - Port 8082 && cd /d %~dp0inventory-service && mvn spring-boot:run"
timeout /t 6 /nobreak >nul
echo [OK] Inventory Service demarre
echo.

REM Billing
echo [7/12] Billing Service - Port 8083...
start "Billing-Service" cmd /k "title Billing Service - Port 8083 && cd /d %~dp0billing-service && mvn spring-boot:run"
timeout /t 6 /nobreak >nul
echo [OK] Billing Service demarre
echo.

REM Supplier
echo [8/12] Supplier Service - Port 8084...
start "Supplier-Service" cmd /k "title Supplier Service - Port 8084 && cd /d %~dp0supplier-service && mvn spring-boot:run"
timeout /t 6 /nobreak >nul
echo [OK] Supplier Service demarre
echo.

REM MCP
echo [9/12] MCP Server - Port 8989...
start "MCP-Server" cmd /k "title MCP Server - Port 8989 && cd /d %~dp0chatbot-service\mcp-server && mvn spring-boot:run"
timeout /t 6 /nobreak >nul
echo [OK] MCP Server demarre
echo.

REM Chatbot
echo [10/12] Chatbot Service - Port 8087...
start "Chatbot-Service" cmd /k "title Chatbot Service - Port 8087 && cd /d %~dp0chatbot-service && mvn spring-boot:run"
timeout /t 6 /nobreak >nul
echo [OK] Chatbot Service demarre
echo.

REM Data Analytics
echo [11/12] Data Analytics - Port 8090...
start "Data-Analytics" cmd /k "title Data Analytics - Port 8090 && cd /d %~dp0data-analytics-service && mvn spring-boot:run"
timeout /t 6 /nobreak >nul
echo [OK] Data Analytics demarre
echo.

REM Frontend
echo [12/12] Frontend Angular...
if not exist "frontend\node_modules" (
    echo Installation des dependances...
    cd frontend
    call npm install
    cd ..
)
start "Frontend-Angular" cmd /k "title Frontend Angular && cd /d %~dp0frontend && npm start"
echo [OK] Frontend Angular demarre
echo.

echo ================================================================
echo   TOUS LES SERVICES SONT EN COURS DE DEMARRAGE
echo ================================================================
echo.
echo IMPORTANT: Regardez les fenetres qui se sont ouvertes!
echo Chaque service a sa propre fenetre avec un titre clair.
echo.
echo Attendez 2-3 minutes, puis executez: test-all.bat
echo.
echo URLs:
echo   - Keycloak: http://localhost:8080
echo   - Eureka: http://localhost:8761
echo   - Gateway: http://localhost:8888
echo.
pause
