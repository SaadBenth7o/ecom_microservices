# 🛒 E-Commerce Microservices Application

Application e-commerce complète avec architecture microservices sécurisée par Keycloak.

![Architecture](goalARCHI.jpg)

---

## 🚀 Démarrage Rapide

```bash
.\start-all.bat
```

Ce script lance automatiquement: Keycloak → Kafka → Discovery → Gateway → Tous les services → Frontend

---

## 📋 Prérequis

| Outil | Version | Port |
|-------|---------|------|
| Java JDK | 21+ | - |
| Maven | 3.6+ | - |
| Node.js | 18+ | - |
| Docker Desktop | Latest | - |
| MySQL | 8.x | 3306 |
| Keycloak | 26.x | 8080 |

---

## 🏗️ Architecture

### Vue d'ensemble

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              FRONTEND (Angular 18)                         │
│                            http://localhost:4200                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        GATEWAY SERVICE (Port 8888)                          │
│              Spring Cloud Gateway + OAuth2 Resource Server                  │
│                         JWT Validation via Keycloak                         │
└─────────────────────────────────────────────────────────────────────────────┘
                                       │
          ┌────────────────────────────┼────────────────────────────┐
          ▼                            ▼                            ▼
┌──────────────────┐        ┌──────────────────┐        ┌──────────────────┐
│ CUSTOMER SERVICE │        │ INVENTORY SERVICE│        │  BILLING SERVICE │
│    Port 8081     │        │    Port 8082     │        │    Port 8083     │
│   MySQL DB       │        │   MySQL DB       │        │   MySQL DB       │
└──────────────────┘        └──────────────────┘        └──────────────────┘
          │                            │                            │
          └────────────────────────────┼────────────────────────────┘
                                       ▼
                        ┌──────────────────────────┐
                        │   DISCOVERY SERVICE      │
                        │   (Eureka) Port 8761     │
                        └──────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                           SERVICES ADDITIONNELS                             │
├─────────────────────┬─────────────────────┬─────────────────────────────────┤
│  SUPPLIER SERVICE   │   DATA ANALYTICS    │        CHATBOT SERVICE          │
│    Port 8084        │     Port 8090       │          Port 8087              │
│    Kafka Producer   │   Kafka Consumer    │     Gemini AI + Telegram        │
├─────────────────────┴─────────────────────┴─────────────────────────────────┤
│                           MCP SERVER (Port 8989)                            │
│                    Model Context Protocol for AI Tools                      │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                        SÉCURITÉ & AUTHENTIFICATION                          │
├──────────────────────────────┬──────────────────────────────────────────────┤
│      KEYCLOAK (Port 8080)    │      KEYCLOAK AUTH SERVICE (Port 8085)      │
│   OAuth2/OIDC Server         │   Expose JWT Public Keys                    │
│   Realm: microservices       │   Endpoints: /api/public-key, /api/jwk-set  │
└──────────────────────────────┴──────────────────────────────────────────────┘
```

---

## 📊 Services Backend

| Service | Port | Description | Base de Données | Événements |
|---------|------|-------------|-----------------|------------|
| **Discovery Service** | 8761 | Registry Eureka pour la découverte de services | - | - |
| **Gateway Service** | 8888 | API Gateway avec validation JWT Keycloak | - | - |
| **Customer Service** | 8081 | Gestion des clients (CRUD) | MySQL `microservices_customers` | ✅ Kafka |
| **Inventory Service** | 8082 | Gestion des produits (CRUD) | MySQL `microservices_inventory` | ✅ Kafka |
| **Billing Service** | 8083 | Gestion des factures | MySQL `microservices_billing` | ✅ Kafka |
| **Supplier Service** | 8084 | Simulation fournisseurs | - | Kafka Producer |
| **Data Analytics** | 8090 | Tableau de bord temps réel | - | Kafka Consumer |
| **Chatbot Service** | 8087 | Bot IA (Gemini + Telegram) | - | - |
| **MCP Server** | 8989 | Outils IA pour le chatbot | - | - |
| **Keycloak Auth** | 8085 | Validation JWT et clés publiques | - | - |

---

## 🗄️ Bases de Données

### Migration H2 → MySQL

Le projet a été migré de **H2 (en mémoire)** vers **MySQL** pour la production.

#### Configuration MySQL
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/{database}?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

#### Bases créées automatiquement
- `microservices_customers` - Données clients
- `microservices_inventory` - Produits et stocks  
- `microservices_billing` - Factures et lignes de facture

---

## 🔐 Sécurité Keycloak

### Configuration requise

1. **Démarrer Keycloak**
   ```bash
   cd C:\keycloak-26.4.6\bin
   .\kc.bat start-dev
   ```

2. **Accéder à la console admin**: http://localhost:8080 (admin/admin)

3. **Créer le Realm**: `microservices`

4. **Créer le Client Angular**:
   - Client ID: `angular-client`
   - Access Type: `public`
   - Valid Redirect URIs: `http://localhost:4200/*`
   - Web Origins: `http://localhost:4200`

5. **Créer un utilisateur** avec mot de passe dans Users

### Flux d'authentification

```
Utilisateur → Angular → Keycloak (Login) → JWT Token
     ↓
Angular (avec JWT) → Gateway → Validation JWT → Services Backend
```

---

## 📡 Apache Kafka

### Composants Docker

```yaml
# docker-compose.yml
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.3.0
    ports: ["2181:2181"]
    
  broker:
    image: confluentinc/cp-kafka:7.3.0
    ports: ["9092:9092"]
```

### Topics
- `billing-events` - Événements de facturation
- `inventory-events` - Mouvements de stock
- `customer-events` - Actions clients

---

## 🌐 Frontend Angular

- **Framework**: Angular 18.2.0
- **Port**: Dynamique (affiché au démarrage)
- **Authentification**: Keycloak JS Adapter

### Fonctionnalités
- ✅ CRUD Customers, Products, Bills
- ✅ Dashboard Kafka temps réel (`/kafka`)
- ✅ Authentification OAuth2/OIDC
- ✅ Interface responsive

---

## 🔗 URLs & Endpoints

### Dashboards
| Service | URL |
|---------|-----|
| Eureka | http://localhost:8761 |
| Keycloak | http://localhost:8080 |
| Gateway | http://localhost:8888 |
| phpMyAdmin | http://localhost/phpmyadmin |

### API (via Gateway)
```
GET  /customer-service/api/customers
POST /customer-service/api/customers
GET  /inventory-service/api/products  
POST /inventory-service/api/products
GET  /billing-service/api/bills
POST /billing-service/api/bills
GET  /data-analytics-service/api/kafka/events
```

---

## 📦 Stack Technique

### Backend
- **Spring Boot** 3.3.4+
- **Spring Cloud** 2023.0.3 (Eureka, Gateway)
- **Spring Security OAuth2** Resource Server
- **Spring Data JPA** + MySQL
- **Spring Kafka**
- **OpenFeign** (communication inter-services)

### Frontend
- **Angular** 18.2.0
- **TypeScript** 5.5.x
- **RxJS** 7.8.x
- **Keycloak JS** Adapter

### Infrastructure
- **Keycloak** 26.x (OAuth2/OIDC)
- **MySQL** 8.x
- **Apache Kafka** 7.3.0
- **Docker** (Kafka/Zookeeper)

---

## 🤖 Chatbot IA

### Configuration
Variables d'environnement requises:
```bash
GEMINI_KEY=your_gemini_api_key
TELEGRAM_API_KEY=your_telegram_bot_token
```

### Architecture MCP
Le chatbot utilise le **Model Context Protocol** pour accéder aux données:
- MCP Server expose les outils: `getCustomers`, `getProducts`, `getBills`
- Le Chatbot interroge les services via MCP
- Réponses générées par **Gemini AI**

---

## 📝 Notes Importantes

1. **MySQL** doit être démarré avant les services
2. **Docker** doit être actif pour Kafka/Zookeeper
3. **Keycloak** est lancé automatiquement par `start-all.bat`
4. Les bases de données sont créées automatiquement au premier démarrage

---

## 📂 Structure du Projet

```
Microservices_App/
├── billing-service/        # Service de facturation
├── chatbot-service/        # Bot IA (Gemini + Telegram)
│   └── mcp-server/         # MCP Server pour outils IA
├── customer-service/       # Gestion des clients
├── data-analytics-service/ # Dashboard Kafka
├── discovery-service/      # Eureka Registry
├── frontend/               # Angular 18
├── gateway-service/        # API Gateway + Sécurité
├── inventory-service/      # Gestion des produits
├── keycloak-auth-service/  # Validation JWT
├── supplier-service/       # Simulation fournisseurs
├── docker-compose.yml      # Kafka + Zookeeper
├── start-all.bat           # Script de démarrage
└── README.md               # Cette documentation
```

---

## 👤 Auteur

**Saad Bendahou**

---

*Dernière mise à jour: Janvier 2026*
