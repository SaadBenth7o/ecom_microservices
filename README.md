# 🚀 Microservices Architecture Project

> **Projet d'architecture microservices avec Spring Boot et Spring Cloud**  
> Ce projet démontre une architecture microservices complète avec Service Discovery, API Gateway et services métier.

---

## 📋 Table des matières

- [Architecture](#-architecture)
- [Technologies utilisées](#-technologies-utilisées)
- [Prérequis](#-prérequis)
- [Installation et lancement](#-installation-et-lancement)
- [URLs et points d'accès](#-urls-et-points-daccès)
- [Bases de données H2](#-bases-de-données-h2)
- [APIs disponibles](#-apis-disponibles)
- [Configuration des services](#-configuration-des-services)
- [Monitoring et santé](#-monitoring-et-santé)

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                     DISCOVERY SERVICE                             │
│                    (Eureka Server)                                │
│                    Port: 8761                                     │
│                                                                   │
│  Gère l'enregistrement et la découverte des microservices        │
└────────────────────┬─────────────────────────────────────────────┘
                     │
                     │ Service Registry
                     │
        ┌────────────┴────────────┬─────────────────┐
        │                         │                  │
        ▼                         ▼                  ▼
┌───────────────┐        ┌────────────────┐   ┌─────────────────┐
│   CUSTOMER    │        │   INVENTORY    │   │    GATEWAY      │
│   SERVICE     │        │    SERVICE     │   │    SERVICE      │
│               │        │                │   │                 │
│  Port: 8081   │        │  Port: 8082    │   │  Port: 8888     │
│               │        │                │   │                 │
│  ┌─────────┐  │        │  ┌──────────┐  │   │  Routage des    │
│  │ H2 DB   │  │        │  │  H2 DB   │  │   │  requêtes       │
│  │customers│  │        │  │ products │  │   │                 │
│  └─────────┘  │        │  └──────────┘  │   │  ┌───────────┐  │
│               │        │                │   │  │  Filters  │  │
│  REST API     │        │   REST API     │   │  │  Routing  │  │
│  /api/        │        │   /api/        │   │  └───────────┘  │
│  customers    │        │   products     │   │                 │
└───────────────┘        └────────────────┘   └─────────────────┘
        │                         │                      │
        └─────────────────────────┴──────────────────────┘
                                  │
                                  ▼
                          ┌──────────────┐
                          │   Clients    │
                          │  (Browser,   │
                          │   Postman)   │
                          └──────────────┘
```

### Flux de communication:

1. **Discovery Service** → Tous les services s'y enregistrent au démarrage
2. **Gateway Service** → Interroge Eureka pour découvrir les services disponibles
3. **Customer/Inventory Services** → S'enregistrent automatiquement sur Eureka
4. **Clients** → Accèdent aux services via Gateway (port 8888) ou directement

---

## 🛠️ Technologies utilisées

| Technologie | Version | Usage |
|-------------|---------|-------|
| **Java** | 21 (LTS) | Langage de programmation |
| **Spring Boot** | 3.3.5 | Framework principal |
| **Spring Cloud** | 2023.0.3 | Microservices patterns |
| **Spring Cloud Netflix Eureka** | 4.3.0 | Service Discovery |
| **Spring Cloud Gateway** | 4.3.0 | API Gateway |
| **Spring Data JPA** | 3.3.5 | Persistence |
| **Spring Data REST** | 4.3.0 | REST APIs automatiques |
| **H2 Database** | 2.4.240 | Base de données en mémoire |
| **Lombok** | 1.18.42 | Réduction du code boilerplate |
| **Maven** | 3.x | Gestion des dépendances |

---

## ✅ Prérequis

Avant de lancer le projet, assurez-vous d'avoir:

- ☕ **Java 21** ou supérieur ([Télécharger](https://adoptium.net/))
- 📦 **Maven 3.6+** (ou utilisez le wrapper Maven inclus: `mvnw`)
- 💻 **IDE**: IntelliJ IDEA, Eclipse, ou VS Code
- 🌐 Ports disponibles: **8761**, **8081**, **8082**, **8888**

---

## 🚀 Installation et lancement

### Méthode 1: Lancement via IntelliJ IDEA (Recommandé)

#### 1️⃣ **Importer le projet**
```bash
File → Open → Sélectionnez le dossier Microservices_App
```

#### 2️⃣ **Recharger Maven**
```bash
Clic droit sur pom.xml → Maven → Reload Project
```

#### 3️⃣ **Lancer les services dans l'ORDRE**

**⚠️ IMPORTANT: Respectez cet ordre de démarrage!**

```
1. DiscoveryServiceApplication  (Port 8761) 
   ⏱️ Attendez 30 secondes

2. CustomerServiceApplication   (Port 8081) } En parallèle
   InventoryServiceApplication  (Port 8082) } possible

3. GatewayServiceApplication    (Port 8888)
   ⏱️ Attendez 20 secondes
```

**OU utilisez la configuration "All Microservices" pour tout démarrer d'un coup!**

---

### Méthode 2: Lancement via ligne de commande

#### 1️⃣ **Build du projet**
```bash
cd Microservices_App
mvn clean install -DskipTests
```

#### 2️⃣ **Lancer les services**

**Terminal 1 - Discovery Service:**
```bash
cd discovery-service
mvn spring-boot:run
```

**Terminal 2 - Customer Service:**
```bash
cd customer-service
mvn spring-boot:run
```

**Terminal 3 - Inventory Service:**
```bash
cd inventory-service
mvn spring-boot:run
```

**Terminal 4 - Gateway Service:**
```bash
cd gateway-service
mvn spring-boot:run
```

---

### Méthode 3: Lancement avec les JARs

```bash
# Build
mvn clean package -DskipTests

# Lancement
java -jar discovery-service/target/discovery-service-0.0.1-SNAPSHOT.jar
java -jar customer-service/target/customer-service-0.0.1-SNAPSHOT.jar
java -jar inventory-service/target/inventory-service-0.0.1-SNAPSHOT.jar
java -jar gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar
```

---

## 🌐 URLs et points d'accès

### 📊 **Dashboard Eureka (Service Discovery)**
```
http://localhost:8761
```
👉 Visualisez tous les services enregistrés et leur statut

---

### 🔗 **Accès VIA le Gateway (Port 8888) - Recommandé**

#### Customer Service:
```bash
# Liste tous les clients
GET http://localhost:8888/customer-service/api/customers

# Client spécifique
GET http://localhost:8888/customer-service/api/customers/1

# Avec projection
GET http://localhost:8888/customer-service/api/customers/1?projection=all
```

#### Inventory Service:
```bash
# Liste tous les produits
GET http://localhost:8888/inventory-service/api/products

# Produit spécifique
GET http://localhost:8888/inventory-service/api/products/{id}

# Pagination
GET http://localhost:8888/inventory-service/api/products?page=0&size=10
```

---

### 🔗 **Accès DIRECT aux services (Sans Gateway)**

#### Customer Service (Port 8081):
```bash
GET http://localhost:8081/api/customers
GET http://localhost:8081/api/customers/1
GET http://localhost:8081/api/customers/search
```

#### Inventory Service (Port 8082):
```bash
GET http://localhost:8082/api/products
GET http://localhost:8082/api/products/{uuid}
```

---

## 💾 Bases de données H2

Les deux services utilisent des bases de données H2 en mémoire pour le développement.

### 🗄️ **Customer Service Database**

| Paramètre | Valeur |
|-----------|--------|
| **Console URL** | `http://localhost:8081/h2-console` |
| **JDBC URL** | `jdbc:h2:mem:customers-db` |
| **Username** | `sa` |
| **Password** | *(laisser vide)* |
| **Driver Class** | `org.h2.Driver` |

**Table: CUSTOMER**
```sql
SELECT * FROM CUSTOMER;
```

**Données initiales (5 clients):**
- Sarah Johnson (sarah.johnson@techcorp.com)
- Ahmed El-Mansouri (ahmed.elmansouri@innovate.io)
- Maria Garcia (maria.garcia@globaltech.es)
- Yuki Tanaka (yuki.tanaka@futuresoft.jp)
- Jean Dupont (jean.dupont@enterprise.fr)

---

### 🗄️ **Inventory Service Database**

| Paramètre | Valeur |
|-----------|--------|
| **Console URL** | `http://localhost:8082/h2-console` |
| **JDBC URL** | `jdbc:h2:mem:products-db` |
| **Username** | `sa` |
| **Password** | *(laisser vide)* |
| **Driver Class** | `org.h2.Driver` |

**Table: PRODUCT**
```sql
SELECT * FROM PRODUCT;
```

**Données initiales (6 produits):**
- MacBook Pro 16-inch ($2,499.99 - 45 unités)
- Sony WH-1000XM5 Headphones ($399.99 - 128 unités)
- Samsung Galaxy S24 Ultra ($1,299.00 - 67 unités)
- LG UltraWide Monitor 34-inch ($599.50 - 32 unités)
- Logitech MX Master 3S Mouse ($99.99 - 215 unités)
- iPad Pro 12.9-inch ($1,099.00 - 89 unités)

---

## 📡 APIs disponibles

### Customer Service API

#### Endpoints REST automatiques (Spring Data REST):

```bash
# GET - Liste des clients
GET /api/customers
Response: 200 OK
{
  "_embedded": {
    "customers": [...]
  },
  "page": {...}
}

# GET - Client par ID
GET /api/customers/{id}
Response: 200 OK

# POST - Créer un client
POST /api/customers
Content-Type: application/json
{
  "name": "New Customer",
  "email": "customer@example.com"
}

# PUT - Modifier un client
PUT /api/customers/{id}
Content-Type: application/json
{
  "name": "Updated Name",
  "email": "updated@example.com"
}

# DELETE - Supprimer un client
DELETE /api/customers/{id}
Response: 204 No Content

# GET - Profil de l'API
GET /api/profile/customers
```

---

### Inventory Service API

#### Endpoints REST automatiques:

```bash
# GET - Liste des produits
GET /api/products
Response: 200 OK

# GET - Produit par UUID
GET /api/products/{uuid}
Response: 200 OK

# POST - Créer un produit
POST /api/products
Content-Type: application/json
{
  "id": "uuid-here",
  "name": "Product Name",
  "price": 999.99,
  "quantity": 50
}

# PUT - Modifier un produit
PUT /api/products/{uuid}

# DELETE - Supprimer un produit
DELETE /api/products/{uuid}

# PATCH - Mise à jour partielle
PATCH /api/products/{uuid}

# GET - Profil de l'API
GET /api/profile/products
```

---

## ⚙️ Configuration des services

### Discovery Service (Port 8761)

```properties
spring.application.name=discovery-service
server.port=8761
eureka.client.fetch-registry=false
eureka.client.register-with-eureka=false
```

### Customer Service (Port 8081)

```properties
spring.application.name=customer-service
server.port=8081
spring.datasource.url=jdbc:h2:mem:customers-db
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
spring.data.rest.base-path=/api
```

### Inventory Service (Port 8082)

```properties
spring.application.name=inventory-service
server.port=8082
spring.datasource.url=jdbc:h2:mem:products-db
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
spring.data.rest.base-path=/api
```

### Gateway Service (Port 8888)

```properties
spring.application.name=gateway-service
server.port=8888
spring.cloud.gateway.discovery.locator.enabled=true
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
```

---

## 🏥 Monitoring et santé

### Actuator Endpoints

Tous les services exposent les endpoints Spring Boot Actuator:

```bash
# Discovery Service
http://localhost:8761/actuator
http://localhost:8761/actuator/health

# Customer Service
http://localhost:8081/actuator
http://localhost:8081/actuator/health
http://localhost:8081/actuator/info
http://localhost:8081/actuator/metrics

# Inventory Service
http://localhost:8082/actuator
http://localhost:8082/actuator/health

# Gateway Service
http://localhost:8888/actuator
http://localhost:8888/actuator/health
http://localhost:8888/actuator/gateway/routes
```

---

## 🎯 État d'avancement

### ✅ Fonctionnalités implémentées

- ✅ **Service Discovery** avec Eureka Server
- ✅ **API Gateway** avec Spring Cloud Gateway
- ✅ **Customer Service** - Gestion des clients
- ✅ **Inventory Service** - Gestion des produits
- ✅ **Bases de données H2** pour chaque service
- ✅ **REST APIs** avec Spring Data REST (HATEOAS)
- ✅ **Enregistrement automatique** des services sur Eureka
- ✅ **Routage dynamique** via le Gateway
- ✅ **Load Balancing** (via Ribbon/LoadBalancer)
- ✅ **Health Checks** avec Actuator
- ✅ **Données de test** préchargées

### 🔄 Prochaines étapes (À venir)

- ⏳ Config Server (configuration centralisée)
- ⏳ Circuit Breaker (Resilience4j)
- ⏳ Distributed Tracing (Zipkin/Sleuth)
- ⏳ Security (OAuth2/JWT)
- ⏳ Containerisation (Docker)
- ⏳ Orchestration (Docker Compose / Kubernetes)

---

## 🐛 Troubleshooting

### Problème: Service ne s'enregistre pas sur Eureka

**Solution:**
1. Vérifiez que Discovery Service est démarré
2. Attendez 30 secondes (délai d'enregistrement)
3. Vérifiez les logs du service
4. Vérifiez la configuration Eureka dans `application.properties`

### Problème: Gateway retourne 404

**Solution:**
1. Vérifiez que le service cible est UP sur Eureka
2. Vérifiez le nom du service dans l'URL (`customer-service`, `inventory-service`)
3. Attendez que le Gateway découvre les services (~30s)
4. Consultez: `http://localhost:8888/actuator/gateway/routes`

### Problème: Port déjà utilisé

**Solution:**
```bash
# Windows
netstat -ano | findstr :8761
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8761
kill -9 <PID>
```

---

## 📚 Ressources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Spring Cloud Netflix](https://spring.io/projects/spring-cloud-netflix)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)

---

