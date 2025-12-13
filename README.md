# 🚀 Architecture Microservices - Projet Spring Boot

> **Architecture microservices complète avec Service Discovery, API Gateway, Config Server et services métier**

---

## 🏗️ Architecture du Projet

```
┌─────────────────────────────────────────────────────────────────────┐
│                    DISCOVERY SERVICE                                  │
│                   (Eureka Server)                                    │
│                   Port: 8761                                         │
│                                                                      │
│  Service Registry - Enregistrement de tous les microservices        │
└────────────────────┬────────────────────────────────────────────────┘
                     │
                     │ Service Registry
                     │
    ┌────────────────┴────────────────┬─────────────────┬──────────────┐
    │                                 │                 │              │
    ▼                                 ▼                 ▼              ▼
┌───────────────┐          ┌────────────────┐   ┌──────────────┐  ┌──────────────┐
│   CUSTOMER    │          │   INVENTORY    │   │   BILLING    │  │    GATEWAY   │
│   SERVICE     │          │    SERVICE     │   │   SERVICE    │  │   SERVICE    │
│               │          │                │   │              │  │              │
│  Port: 8081   │          │  Port: 8082    │   │  Port: 8083   │  │  Port: 8888   │
│               │          │                │   │              │  │              │
│  ┌─────────┐  │          │  ┌──────────┐  │   │  ┌─────────┐ │  │  Routage des │
│  │ H2 DB   │  │          │  │  H2 DB   │  │   │  │ H2 DB   │ │  │  requêtes    │
│  │customers│  │          │  │ products │  │   │  │ bills   │ │  │              │
│  └─────────┘  │          │  └──────────┘  │   │  └─────────┘ │  │  ┌─────────┐ │
│               │          │                │   │              │  │  │ Filters │ │
│  REST API     │          │   REST API     │   │  REST API    │  │  │ Routing │ │
│  /api/        │          │   /api/        │   │  /api/       │  │  └─────────┘ │
│  customers    │          │   products     │   │  bills       │  │              │
└───────────────┘          └────────────────┘   └──────────────┘  └──────────────┘
        │                         │                      │              │
        │                         │                      │              │
        └─────────────────────────┴──────────────────────┴──────────────┘
                                  │
                                  ▼
                          ┌──────────────┐
                          │   Clients    │
                          │  (Browser,   │
                          │   Postman)   │
                          └──────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                    CONFIG SERVICE                                    │
│                   (Config Server)                                    │
│                   Port: 9999                                         │
│                                                                      │
│  Configuration centralisée depuis config-repo/                      │
│  (Optionnel - les services peuvent fonctionner sans)                │
└────────────────────┬────────────────────────────────────────────────┘
                     │
                     │ Configuration
                     │
        ┌────────────┴────────────┬─────────────────┬──────────────┐
        ▼                         ▼                  ▼              ▼
┌───────────────┐          ┌────────────────┐   ┌──────────────┐  ┌──────────────┐
│   CUSTOMER    │          │   INVENTORY    │   │   BILLING    │  │    GATEWAY   │
│   SERVICE     │          │    SERVICE     │   │   SERVICE    │  │   SERVICE    │
└───────────────┘          └────────────────┘   └──────────────┘  └──────────────┘
```

### 📊 Microservices du Projet

| Service | Port | Description | Base de données |
|---------|------|-------------|-----------------|
| **Discovery Service** | 8761 | Eureka Server - Registre de services | - |
| **Config Service** | 9999 | Configuration centralisée (optionnel) | - |
| **Customer Service** | 8081 | Gestion des clients | H2 (customers-db) |
| **Inventory Service** | 8082 | Gestion des produits | H2 (products-db) |
| **Billing Service** | 8083 | Gestion des factures (utilise OpenFeign) | H2 (bills-db) |
| **Gateway Service** | 8888 | API Gateway - Point d'entrée unique | - |

### 🔄 Flux de Communication

1. **Discovery Service** → Tous les services s'y enregistrent au démarrage
2. **Config Service** → Fournit la configuration centralisée (optionnel)
3. **Gateway Service** → Interroge Eureka pour découvrir les services disponibles
4. **Customer/Inventory/Billing Services** → S'enregistrent automatiquement sur Eureka
5. **Billing Service** → Utilise OpenFeign pour appeler Customer et Inventory Services
6. **Clients** → Accèdent aux services via Gateway (port 8888) ou directement

---

## 🚀 Comment Lancer les Microservices

### ⚠️ Ordre de Démarrage IMPORTANT

**Respectez cet ordre pour éviter les erreurs de connexion:**

```
1️⃣ DiscoveryServiceApplication  (Port 8761) 
   ⏱️ Attendez 30 secondes qu'il démarre complètement

2️⃣ ConfigServiceApplication     (Port 9999) - Optionnel
   ⏱️ Attendez 10 secondes

3️⃣ CustomerServiceApplication   (Port 8081) } 
   InventoryServiceApplication   (Port 8082) } En parallèle
   BillingServiceApplication     (Port 8083) } possible

4️⃣ GatewayServiceApplication    (Port 8888)
   ⏱️ Attendez 20 secondes que les autres services soient enregistrés
```

---

### Méthode 1: Via IntelliJ IDEA (Recommandé)

#### 1. **Importer le projet**
```
File → Open → Sélectionnez le dossier Microservices_App
```

#### 2. **Recharger Maven**
```
Clic droit sur pom.xml → Maven → Reload Project
```

#### 3. **Lancer les services**

**Option A: Lancement individuel**
- Ouvrez **Run → Edit Configurations...**
- Lancez chaque service dans l'ordre indiqué ci-dessus
- OU utilisez la configuration **"All Microservices"** pour tout démarrer d'un coup

**Option B: Configuration "All Microservices"**
- Dans la liste des configurations, sélectionnez **"All Microservices"**
- Cliquez sur ▶️ pour démarrer tous les services en une fois

---

### Méthode 2: Via Ligne de Commande Maven

#### 1. **Build du projet**
```bash
cd Microservices_App
mvn clean install -DskipTests
```

#### 2. **Lancer les services dans des terminaux séparés**

**Terminal 1 - Discovery Service:**
```bash
cd discovery-service
mvn spring-boot:run
```

**Terminal 2 - Config Service (Optionnel):**
```bash
cd config-service
mvn spring-boot:run
```

**Terminal 3 - Customer Service:**
```bash
cd customer-service
mvn spring-boot:run
```

**Terminal 4 - Inventory Service:**
```bash
cd inventory-service
mvn spring-boot:run
```

**Terminal 5 - Billing Service:**
```bash
cd billing-service
mvn spring-boot:run
```

**Terminal 6 - Gateway Service:**
```bash
cd gateway-service
mvn spring-boot:run
```

---

### Méthode 3: Via JARs Compilés

```bash
# Build
mvn clean package -DskipTests

# Lancement (dans l'ordre)
java -jar discovery-service/target/discovery-service-0.0.1-SNAPSHOT.jar
java -jar config-service/target/config-service-0.0.1-SNAPSHOT.jar
java -jar customer-service/target/customer-service-0.0.1-SNAPSHOT.jar
java -jar inventory-service/target/inventory-service-0.0.1-SNAPSHOT.jar
java -jar billing-service/target/billing-service-0.0.1-SNAPSHOT.jar
java -jar gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar
```

---

## 🌐 URLs et Points d'Accès

### 📊 Eureka Dashboard (Service Discovery)
```
http://localhost:8761
```
👉 Visualisez tous les services enregistrés et leur statut

---

### 🔗 Accès VIA le Gateway (Port 8888) - Recommandé

```bash
# Customer Service
http://localhost:8888/customer-service/api/customers
http://localhost:8888/customer-service/api/customers/{id}

# Inventory Service
http://localhost:8888/inventory-service/api/products
http://localhost:8888/inventory-service/api/products/{uuid}

# Billing Service
http://localhost:8888/billing-service/api/bills
http://localhost:8888/billing-service/api/bills/{id}
```

---

### 🔗 Accès DIRECT aux Services (Sans Gateway)

```bash
# Customer Service (Port 8081)
http://localhost:8081/api/customers
http://localhost:8081/api/customers/{id}

# Inventory Service (Port 8082)
http://localhost:8082/api/products
http://localhost:8082/api/products/{uuid}

# Billing Service (Port 8083)
http://localhost:8083/api/bills
http://localhost:8083/api/bills/{id}

# Config Service (Port 9999)
http://localhost:9999/{application}/{profile}
# Exemple: http://localhost:9999/billing-service/default
```

---

## 💾 Bases de Données H2

### Customer Service Database
- **Console:** `http://localhost:8081/h2-console`
- **JDBC URL:** `jdbc:h2:mem:customers-db`
- **Username:** `sa`
- **Password:** *(vide)*

### Inventory Service Database
- **Console:** `http://localhost:8082/h2-console`
- **JDBC URL:** `jdbc:h2:mem:products-db`
- **Username:** `sa`
- **Password:** *(vide)*

### Billing Service Database
- **Console:** `http://localhost:8083/h2-console`
- **JDBC URL:** `jdbc:h2:mem:bills-db`
- **Username:** `sa`
- **Password:** *(vide)*

---

## 🛠️ Technologies Utilisées

- **Java 21** (LTS)
- **Spring Boot 3.3.5**
- **Spring Cloud 2023.0.3**
- **Spring Cloud Netflix Eureka** (Service Discovery)
- **Spring Cloud Gateway** (API Gateway)
- **Spring Cloud Config Server** (Configuration centralisée)
- **Spring Cloud OpenFeign** (Communication inter-services)
- **Spring Data JPA & REST** (Persistence et APIs)
- **H2 Database** (Base de données en mémoire)
- **Maven** (Gestion des dépendances)

---

## ✅ Prérequis

- ☕ **Java 21** ou supérieur
- 📦 **Maven 3.6+** (ou utilisez le wrapper Maven inclus: `mvnw`)
- 💻 **IDE**: IntelliJ IDEA (recommandé), Eclipse, ou VS Code
- 🌐 **Ports disponibles:** 8761, 8081, 8082, 8083, 8888, 9999

---

## 🎯 Fonctionnalités

- ✅ **Service Discovery** avec Eureka Server
- ✅ **API Gateway** avec routage dynamique
- ✅ **Config Server** pour configuration centralisée
- ✅ **Customer Service** - Gestion des clients
- ✅ **Inventory Service** - Gestion des produits
- ✅ **Billing Service** - Gestion des factures (utilise OpenFeign)
- ✅ **Bases de données H2** pour chaque service
- ✅ **REST APIs** avec Spring Data REST (HATEOAS)
- ✅ **Enregistrement automatique** sur Eureka
- ✅ **Load Balancing** automatique
- ✅ **Health Checks** avec Actuator

---

## 📝 Notes Importantes

1. **Ordre de démarrage:** Toujours démarrer Discovery Service en premier!
2. **Temps de démarrage:** Chaque service met ~20-30 secondes à démarrer
3. **Enregistrement Eureka:** Les services mettent ~30 secondes supplémentaires à s'enregistrer
4. **Billing Service:** Génère automatiquement des factures au démarrage en appelant Customer et Inventory Services via OpenFeign
5. **Config Service:** Optionnel - les services peuvent fonctionner sans, mais utilisent la configuration locale par défaut

---

**🎉 Bon développement avec les microservices!**

