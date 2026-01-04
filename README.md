# 🛒 E-Commerce Microservices Application

Application e-commerce complète avec architecture microservices Spring Boot, Keycloak, Kafka et chatbot IA.

---

## 📐 Architecture

Diagramme de l'architecture globale du système:

![Architecture du Système](docs/images/00_architecture.jpg)

---

## 🗄️ Bases de Données H2

Les microservices utilisent des bases de données H2 en mémoire pour le développement.

### Customer Database
Console H2 du service Customer montrant la table des clients:

![Console H2 - Customers DB](docs/images/01_h2_customers_db.png)

### Inventory Database
Console H2 du service Inventory montrant la table des produits:

![Console H2 - Inventory DB](docs/images/02_h2_inventory_db.png)

### Billing Database
Console H2 du service Billing montrant les tables de facturation:

![Console H2 - Billing DB](docs/images/03_h2_billing_db.png)

---

## 📊 Eureka Dashboard

Service Discovery avec Netflix Eureka montrant tous les microservices enregistrés:

![Eureka Dashboard](docs/images/04_eureka_dashboard.png)

---

## 🔐 Keycloak - Authentification

Configuration et interface d'authentification avec Keycloak.

### Console d'Administration Keycloak
Interface d'administration Keycloak montrant la gestion des clients:

![Keycloak Admin - Clients](docs/images/05_keycloak_admin_clients.png)

### Page de Connexion Keycloak
Page de connexion pour l'administration Keycloak:

![Keycloak Login](docs/images/06_keycloak_login.png)

### Page de Connexion OIDC
Page de connexion OpenID Connect pour l'application Angular:

![Keycloak OIDC Login](docs/images/12_keycloak_oidc_login.png)

---

## 🌐 Interface Utilisateur (Frontend Angular)

### Page d'Accueil / Dashboard
Page principale de l'application avec vue d'ensemble (customers, products, bills):

![Frontend - Dashboard](docs/images/07_frontend_dashboard.png)

### Gestion des Clients
Interface de gestion des clients avec liste et actions:

![Frontend - Customers](docs/images/08_frontend_customers.png)

### Gestion des Produits
Interface de gestion des produits avec liste et actions:

![Frontend - Products](docs/images/09_frontend_products.png)

### Gestion des Factures
Interface de gestion des factures avec liste et actions:

![Frontend - Bills](docs/images/10_frontend_bills.png)

### Dashboard Kafka Stream
Interface de monitoring des événements Kafka en temps réel:

![Frontend - Kafka Stream](docs/images/11_frontend_kafka_stream.png)

---

## 🔗 API REST Endpoints

Démonstration des endpoints REST exposés par les microservices avec réponses JSON.

### API Customers
Réponse JSON de l'endpoint `/api/customers`:

![API - Customers JSON](docs/images/13_api_customers.png)

### API Products
Réponse JSON de l'endpoint `/api/products`:

![API - Products JSON](docs/images/14_api_products.png)

### API Bills
Réponse JSON de l'endpoint `/api/bills`:

![API - Bills JSON](docs/images/15_api_bills.png)

---

## 🤖 Chatbot Telegram (Gemini AI)

Bot Telegram intégré avec Gemini AI pour assistance client. Le bot propose **trois modes de fonctionnement** via des boutons interactifs.

### Modes de Fonctionnement

| Mode | Description |
|------|-------------|
| 🗄️ **Base de Données** | Consultation des clients, produits et factures via MCP Server (Billing, Customer, Inventory) |
| 📋 **Politiques** | Questions sur les politiques d'entreprise (retours, livraisons, garanties) via RAG |
| 📷 **Analyse d'Images** | Description et analyse détaillée d'images envoyées par l'utilisateur avec Gemini Vision |

### Conversations avec le Bot

<div style="display: flex; gap: 20px; justify-content: center; flex-wrap: wrap;">
  <div style="flex: 1; min-width: 300px; max-width: 400px;">
    <p><strong>Requête basée sur les bases de données MCP</strong></p>
    <p>Le bot interroge les microservices Billing, Customer et Inventory via MCP (Model Context Protocol) pour répondre aux questions sur les données de l'application.</p>
    <img src="docs/images/16_telegram_bot_1.jpg" alt="Telegram Bot - MCP Database Query" style="width: 100%; height: auto; border-radius: 8px;">
  </div>
  <div style="flex: 1; min-width: 300px; max-width: 400px;">
    <p><strong>Requête basée sur RAG (Retrieval Augmented Generation)</strong></p>
    <p>Le bot utilise RAG pour répondre aux questions basées sur le fichier de politique interne de l'entreprise, permettant des réponses contextuelles précises.</p>
    <img src="docs/images/17_telegram_bot_2.jpg" alt="Telegram Bot - RAG Policy Query" style="width: 100%; height: auto; border-radius: 8px;">
  </div>
  <div style="flex: 1; min-width: 300px; max-width: 400px;">
    <p><strong>Analyse d'Images avec Gemini Vision</strong></p>
    <p>Le bot analyse et décrit les images envoyées par les utilisateurs de manière simple et naturelle, comme un humain le ferait.</p>
    <img src="docs/images/18_telegram_image_analysis.png" alt="Telegram Bot - Image Analysis" style="width: 100%; height: auto; border-radius: 8px;">
  </div>
</div>

### Fonctionnalité d'Analyse d'Images

Le bot peut analyser et décrire des images envoyées par les utilisateurs :

- **Envoi d'image seule** : Le bot fournit une description simple et naturelle (2-3 phrases)
- **Image avec légende** : L'utilisateur peut poser une question spécifique sur l'image (ex: "Qu'est-ce que vous voyez ?", "Décrivez-moi cette image")
- **Analyse intelligente** : Utilise Gemini Vision API pour identifier les éléments principaux dans l'image
- **Descriptions naturelles** : Répond de manière conversationnelle, comme un humain le ferait



---

## 🚀 Démarrage Rapide

```bash
.\start-all.bat
```

### Services et Ports

| Service | Port | Description |
|---------|------|-------------|
| Eureka | 8761 | Service Discovery |
| Gateway | 8888 | API Gateway |
| Customer | 8081 | Gestion Clients |
| Inventory | 8082 | Gestion Produits |
| Billing | 8083 | Facturation |
| Chatbot | 8087 | Bot IA Telegram |
| MCP Server | 8989 | Outils IA |
| Keycloak | 8080 | Authentification |

### Consoles H2

| Service | URL | JDBC URL | User |
|---------|-----|----------|------|
| Customer | http://localhost:8081/h2-console | `jdbc:h2:mem:customersdb` | `sa` |
| Inventory | http://localhost:8082/h2-console | `jdbc:h2:mem:inventorydb` | `sa` |
| Billing | http://localhost:8083/h2-console | `jdbc:h2:mem:billingdb` | `sa` |

---

## 👤 Auteur

**Saad Bendahou**

*Janvier 2026*
