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
| H2 Database | Embedded | In-Memory |
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
│   H2 Database    │        │   H2 Database    │        │   H2 Database    │
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
| **Customer Service** | 8081 | Gestion des clients (CRUD) | H2 `customersdb` | ✅ Kafka |
| **Inventory Service** | 8082 | Gestion des produits (CRUD) | H2 `inventorydb` | ✅ Kafka |
| **Billing Service** | 8083 | Gestion des factures | H2 `billingdb` | ✅ Kafka |
| **Supplier Service** | 8084 | Simulation fournisseurs | - | Kafka Producer |
| **Data Analytics** | 8090 | Tableau de bord temps réel | - | Kafka Consumer |
| **Chatbot Service** | 8087 | Bot IA (Gemini + Telegram) | - | - |
| **MCP Server** | 8989 | Outils IA pour le chatbot | - | - |
| **Keycloak Auth** | 8085 | Validation JWT et clés publiques | - | - |

---

## 🗄️ Bases de Données H2

Le projet utilise **H2 Database** en mémoire pour faciliter le développement et les démonstrations.

#### Configuration H2
```properties
spring.datasource.url=jdbc:h2:mem:{dbname}
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

#### Consoles H2
| Service | URL | JDBC URL |
|---------|-----|----------|
| Customer | http://localhost:8081/h2-console | `jdbc:h2:mem:customersdb` |
| Inventory | http://localhost:8082/h2-console | `jdbc:h2:mem:inventorydb` |
| Billing | http://localhost:8083/h2-console | `jdbc:h2:mem:billingdb` |

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
- **MySQL** 8.x
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

## 🤖 Chatbot IA (Telegram)

### Configuration
Variables d'environnement requises:
```bash
GEMINI_KEY=your_gemini_api_key
TELEGRAM_API_KEY=your_telegram_bot_token
```

### Trois Modes de Fonctionnement

Le chatbot propose **trois modes** via des boutons interactifs:

| Mode | Description |
|------|-------------|
| 🗄️ **Base de Données** | Consultation clients, produits, factures via MCP Server (Billing, Customer, Inventory) |
| 📋 **Politiques** | Questions sur retours, livraison, garanties via RAG (Retrieval-Augmented Generation) |
| 📷 **Analyse d'Images** | Description et analyse d'images envoyées par l'utilisateur avec Gemini Vision API |

### Architecture du Chatbot

Le chatbot utilise un **agent unique (AIAgent)** qui gère toutes les fonctionnalités :

#### Structure des Agents
```
chatbot-service/
└── agents/
    └── AIAgent.java  → Gère tout (MCP + RAG + Images)
```

#### Architecture MCP
- **MCP Server** expose les outils: `getCustomers`, `getProducts`, `getBills`
- Les réponses sont générées par **Gemini AI**
- Utilise `ChatClient` avec `MessageChatMemoryAdvisor` pour maintenir le contexte

### Mode RAG (Politiques d'Entreprise)
Le mode Politiques utilise **Retrieval-Augmented Generation**:
- Répond **uniquement** basé sur le document `policies.txt`
- Contenu: Retours (14 jours), Livraison (25-50 MAD), Garanties (2 ans), CGV
- Rejette les questions hors sujet

### Mode Analyse d'Images
Le mode Analyse d'Images utilise **Gemini Vision API**:
- Analyse les images envoyées par les utilisateurs via Telegram
- Fournit des descriptions courtes et naturelles (2-3 phrases par défaut)
- Supporte les questions spécifiques via légendes d'images
- Utilise un ChatClient séparé sans mémoire pour éviter la persistance des images
- Ne nécessite pas les outils MCP (pas de conflit avec les outils de base de données)

---

## 💰 Monnaie

Tous les prix sont affichés en **Dirhams Marocains (MAD)**.

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
│   ├── agents/
│   │   └── AIAgent.java    # Agent unique (MCP + RAG + Images)
│   ├── telegram/
│   │   └── TelegramBot.java # Gestion des messages Telegram
│   ├── service/
│   │   └── UserSessionService.java # Gestion des modes utilisateur
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
