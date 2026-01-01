@echo off
chcp 65001 >nul
color 0B
cls

echo ================================================================
echo   TESTS COMPLETS DES SERVICES
echo ================================================================
echo.

set /a success=0
set /a failed=0

REM Test Eureka
echo [TEST 1] Eureka Discovery Service (port 8761)...
curl -s -o nul -w "%%{http_code}" http://localhost:8761 >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Eureka est ACTIF
    set /a success+=1
) else (
    echo [ERREUR] Eureka NON DISPONIBLE
    set /a failed+=1
)
echo.

REM Test Gateway
echo [TEST 2] Gateway Service (port 8888)...
curl -s -o nul -w "%%{http_code}" http://localhost:8888/actuator/health >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Gateway est ACTIF
    set /a success+=1
) else (
    echo [ERREUR] Gateway NON DISPONIBLE
    set /a failed+=1
)
echo.

REM Test Customer
echo [TEST 3] Customer Service (port 8081)...
curl -s http://localhost:8081/api/customers >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Customer Service est ACTIF
    curl -s http://localhost:8081/api/customers | findstr /C:"name" >nul
    if %errorlevel% equ 0 (
        echo       Donnees trouvees
    )
    set /a success+=1
) else (
    echo [ERREUR] Customer Service NON DISPONIBLE
    set /a failed+=1
)
echo.

REM Test Inventory
echo [TEST 4] Inventory Service (port 8082)...
curl -s http://localhost:8082/api/products >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Inventory Service est ACTIF
    set /a success+=1
) else (
    echo [ERREUR] Inventory Service NON DISPONIBLE
    set /a failed+=1
)
echo.

REM Test Billing
echo [TEST 5] Billing Service (port 8083)...
curl -s http://localhost:8083/api/bills >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Billing Service est ACTIF
    set /a success+=1
) else (
    echo [ERREUR] Billing Service NON DISPONIBLE
    set /a failed+=1
)
echo.

REM Test Data Analytics
echo [TEST 6] Data Analytics Service (port 8090)...
curl -s http://localhost:8090/api/kafka/events/count >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Data Analytics est ACTIF
    set /a success+=1
) else (
    echo [ERREUR] Data Analytics NON DISPONIBLE
    set /a failed+=1
)
echo.

REM Test Kafka
echo [TEST 7] Kafka + Zookeeper...
docker ps --filter "name=broker" --filter "name=zookeeper" --format "{{.Names}}" >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Kafka et Zookeeper sont ACTIFS
    set /a success+=1
) else (
    echo [ERREUR] Kafka/Zookeeper NON DISPONIBLES
    set /a failed+=1
)
echo.

REM Test CRUD - Create
echo [TEST 8] Test CRUD - Creation Client...
curl -s -X POST http://localhost:8081/api/customers -H "Content-Type: application/json" -d "{\"name\":\"Test User\",\"email\":\"test@test.com\"}" >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Creation client reussie
    set /a success+=1
) else (
    echo [ERREUR] Echec creation client
    set /a failed+=1
)
echo.

REM Test Gateway Routing
echo [TEST 9] Gateway Routing - Customer Service...
curl -s http://localhost:8888/CUSTOMER-SERVICE/api/customers >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Gateway routing fonctionne
    set /a success+=1
) else (
    echo [ERREUR] Gateway routing ne fonctionne pas
    set /a failed+=1
)
echo.

REM Test Kafka Events
echo [TEST 10] Kafka Events...
curl -s http://localhost:8090/api/kafka/events/count 2>nul | findstr "totalEvents" >nul
if %errorlevel% equ 0 (
    echo [OK] Kafka events accessibles
    set /a success+=1
) else (
    echo [ERREUR] Kafka events non accessibles
    set /a failed+=1
)
echo.

echo ================================================================
echo   RESUME DES TESTS
echo ================================================================
echo.
echo Tests reussis: %success%
echo Tests echoues: %failed%
echo.
if %failed% gtr 0 (
    echo [ATTENTION] Certains services ne sont pas disponibles.
    echo Verifiez les fenetres des services pour voir les erreurs.
    echo.
    echo Si les services viennent de demarrer, attendez 1-2 minutes
    echo et relancez ce script.
) else (
    echo [SUCCES] Tous les tests sont passes!
)
echo.
echo URLs importantes:
echo   - Eureka: http://localhost:8761
echo   - Gateway: http://localhost:8888
echo   - Customer API: http://localhost:8081/api/customers
echo   - Products API: http://localhost:8082/api/products
echo   - Bills API: http://localhost:8083/api/bills
echo   - Kafka Events: http://localhost:8090/api/kafka/events
echo.
pause
