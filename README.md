# E-Commerce Microservices Application

Application e-commerce complète utilisant une architecture microservices avec Spring Boot et Angular 18.

## Architecture

### Backend - Microservices Spring Boot

```
┌─────────────────────────────────────────┐
│         Frontend Angular 18              │
│         http://localhost:PORT            │
└────────────────┬────────────────────────┘
                 │ HTTP
                 ▼
┌─────────────────────────────────────────┐
│       API Gateway (Port 8888)           │
│       + CORS Configuration              │
└──┬──────────┬──────────┬────────────────┘
   │          │          │
   ▼          ▼          ▼
┌─────────┐ ┌──────────┐ ┌────────┐
│Customer │ │Inventory │ │Billing │
│Service  │ │Service   │ │Service │
│  :8081  │ │  :8082   │ │  :8083 │
└────┬────┘ └─────┬────┘ └───┬────┘
     │            │           │
     └────────────┴───────────┘
                  │
          ┌───────▼───────┐
          │    Eureka     │
          │   Discovery   │
          │     :8761     │
          └───────────────┘
```

### Services

| Service | Port | Description |
|---------|------|-------------|
| **config-service** | 8888 | Configuration centralisée (Spring Cloud Config) |
| **discovery-service** | 8761 | Service registry (Eureka Server) |
| **gateway-service** | 8888 | API Gateway + CORS + Routing |
| **customer-service** | 8081 | Gestion des clients |
| **inventory-service** | 8082 | Gestion du catalogue produits |
| **billing-service** | 8083 | Gestion des factures |

### Frontend

- **Framework** : Angular 18.2.0 (standalone components)
- **Design** : Minimaliste et moderne
- **Pages** : Dashboard, Customers, Products, Bills
- **Port** : Dynamique (assigné automatiquement par Angular CLI)

---

## Prérequis

- **Java** : JDK 21 ou supérieur
- **Maven** : 3.6+
- **Node.js** : v18+ et npm
- **Git** : Pour cloner le config-repo

---

## Lancement de l'Application

### Option 1 : Script Automatique (Recommandé)

```bash
.\start-all.bat
```

Le script lance automatiquement tous les services dans l'ordre correct avec les délais appropriés.

### Option 2 : Lancement Manuel

```bash
# 1. Config Service (attendre 15s)
cd config-service
mvn spring-boot:run

# 2. Discovery Service (attendre 20s)
cd discovery-service
mvn spring-boot:run

# 3. Gateway Service (attendre 20s)
cd gateway-service
mvn spring-boot:run

# 4. Customer Service
cd customer-service
mvn spring-boot:run

# 5. Inventory Service
cd inventory-service
mvn spring-boot:run

# 6. Billing Service
cd billing-service
mvn spring-boot:run

# 7. Frontend Angular
cd frontend
npm start
```

---

## URLs Importantes

| Service/Interface | URL | Description |
|-------------------|-----|-------------|
| **Frontend** | `http://localhost:PORT` | Interface utilisateur (voir terminal pour le port exact) |
| **Eureka Dashboard** | http://localhost:8761 | Monitoring des services |
| **Gateway** | http://localhost:8888 | Point d'entrée API |
| **Gateway Health** | http://localhost:8888/actuator/health | Santé du Gateway |

### Endpoints Backend (via Gateway)

```
# Clients
http://localhost:8888/CUSTOMER-SERVICE/api/customers

# Produits
http://localhost:8888/INVENTORY-SERVICE/api/products

# Factures
http://localhost:8888/BILLING-SERVICE/api/bills
```

---

## Données de Test

L'application contient des données de démonstration :

### Customers (3)
- Mohammed (mohammed@gmail.com)
- Larbi (Larbi@gmail.com)
- Oussama (Oussama@gmail.com)

### Products (3)
- Computer - $6500.00 (321 unités)
- Printer - $5400.00 (19 unités)
- Smart Phone - $4300.00 (14 unités)

---

## Vérification du Démarrage

### 1. Vérifier Eureka

Ouvrir http://localhost:8761

**Attendu** : Voir 4-5 services enregistrés comme **UP** :
- GATEWAY-SERVICE
- CUSTOMER-SERVICE
- INVENTORY-SERVICE
- BILLING-SERVICE

### 2. Vérifier le Frontend

Consulter la fenêtre terminal "**Frontend Angular 18**" pour trouver l'URL :

```
➜ Local: http://localhost:XXXXX/
```

Ouvrir cette URL dans le navigateur.

**Attendu** :
- Dashboard avec statistiques (3 customers, 3 products)
- Navigation sidebar fonctionnelle
- Pas d'erreurs CORS dans la console

---

## Structure du Projet

```
Microservices_App/
├── config-service/          # Configuration centralisée
├── discovery-service/        # Eureka Server
├── gateway-service/          # API Gateway + CORS
├── customer-service/         # Microservice Clients
├── inventory-service/        # Microservice Produits
├── billing-service/          # Microservice Factures
├── frontend/                 # Application Angular 18
├── config-repo/              # Repository de configuration
├── start-all.bat            # Script de lancement automatique
└── README.md                # Ce fichier
```

---

## Technologies Utilisées

### Backend
- **Spring Boot** 3.3.5
- **Spring Cloud**
  - Config Server
  - Eureka Discovery
  - Gateway
- **Spring Data REST**
- **H2 Database** (en mémoire)
- **Feign Client** (pour communication inter-services)

### Frontend
- **Angular** 18.2.0
- **TypeScript** 5.5.x
- **RxJS** 7.8.x
- **CSS** Vanilla (design moderne)

---

## Configuration CORS

Le Gateway est configuré pour accepter les requêtes depuis n'importe quel port localhost (développement) :

```java
// gateway-service/src/main/java/org/saad/gatewayservice/config/CorsConfig.java
corsConfig.setAllowedOriginPatterns(
    Collections.singletonList("http://localhost:*")
);
```

---

## Résolution de Problèmes

### Port Déjà Utilisé

```bash
# Trouver le processus utilisant un port
netstat -ano | findstr :8081

# Arrêter le processus
taskkill /F /PID [process-id]
```

### Frontend - Port Dynamique

Angular CLI assigne automatiquement un port disponible. Consultez toujours la sortie terminal pour l'URL exacte.

### Services Non Enregistrés dans Eureka

Attendez 30-60 secondes (heartbeat interval). Si le problème persiste :
1. Vérifiez que Discovery Service est démarré
2. Vérifiez les logs du service concerné

---

## Développement

### Compilation d'un Service

```bash
cd [service-name]
mvn clean compile
```

### Tests

```bash
mvn test
```

### Package

```bash
mvn clean package
```

---

## Auteur

**Saad** (org.saad)

---

## License

Ce projet est à des fins éducatives.
