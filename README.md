# E-Commerce Microservices Application

Application e-commerce complète avec architecture microservices (Spring Boot + Angular 18).

## 🚀 Démarrage Rapide

```bash
.\start-all.bat
```

Le script lance automatiquement tous les services.

## 📋 Prérequis

- **Java** JDK 21+
- **Maven** 3.6+
- **Node.js** v18+ et npm
- **Docker** Desktop (pour Kafka)
- **MySQL** (port 3306)
- **Keycloak** (optionnel, port 8080)

## 🏗️ Architecture

### Services Backend

| Service | Port | Base de Données |
|---------|------|-----------------|
| Discovery Service (Eureka) | 8761 | - |
| Gateway Service | 8888 | - |
| Customer Service | 8081 | MySQL |
| Inventory Service | 8082 | MySQL |
| Billing Service | 8083 | MySQL |
| Supplier Service | 8084 | Kafka |
| MCP Server | 8989 | - |
| Chatbot Service | 8087 | - |
| Data Analytics Service | 8090 | Kafka |

### Frontend

- **Angular 18** (port dynamique)
- Interface CRUD complète pour Customers, Products, Bills
- Dashboard Kafka en temps réel

## 🔗 URLs Importantes

- **Eureka Dashboard**: http://localhost:8761
- **Gateway**: http://localhost:8888
- **phpMyAdmin**: http://localhost/phpmyadmin
- **Keycloak**: http://localhost:8080 (admin/123)
- **Data Analytics**: http://localhost:8090/api/kafka/events

### Endpoints API (via Gateway)

- Customers: `http://localhost:8888/CUSTOMER-SERVICE/api/customers`
- Products: `http://localhost:8888/INVENTORY-SERVICE/api/products`
- Bills: `http://localhost:8888/BILLING-SERVICE/api/bills`

## 🗄️ Bases de Données MySQL

Les bases de données sont créées automatiquement:
- `microservices_customers`
- `microservices_inventory`
- `microservices_billing`

## 🔐 Sécurité

- **Keycloak** pour l'authentification OAuth2/JWT
- Endpoints `/api/**` accessibles pour MCP (chatbot)

## 📦 Technologies

### Backend
- Spring Boot 3.3.5+
- Spring Cloud (Eureka, Gateway)
- MySQL
- Kafka
- Keycloak

### Frontend
- Angular 18.2.0
- TypeScript 5.5.x
- RxJS 7.8.x

## 🧪 Tests

Après démarrage, vérifiez:
1. Eureka Dashboard: tous les services doivent être "UP"
2. Frontend Angular: URL dans la fenêtre terminal
3. Kafka Dashboard: route `/kafka` dans le frontend

## 📝 Notes

- MySQL doit être démarré avant les services
- Keycloak peut être démarré manuellement si nécessaire
- Variables d'environnement pour le chatbot: `GEMINI_KEY`, `TELEGRAM_API_KEY`
