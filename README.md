# Application Microservices K8 - Documentation

Cette application microservices est conçue pour gérer [décrire le domaine métier de votre application]. Elle offre une architecture distribuée moderne basée sur Spring Boot et Spring Cloud, avec une interface utilisateur Angular et une sécurisation via Keycloak. L'application vise à fournir une solution scalable, résiliente et facilement maintenable.

## Technologies Utilisées

- <img src="https://www.vectorlogo.zone/logos/springio/springio-icon.svg" width="30" height="30"/> &nbsp;&nbsp;**Spring Boot**
- <img src="https://www.vectorlogo.zone/logos/springio/springio-icon.svg" width="30" height="30"/> &nbsp;&nbsp;**Spring Cloud**
- <img src="https://www.vectorlogo.zone/logos/angular/angular-icon.svg" width="30" height="30"/> &nbsp;&nbsp;**Angular**
- <img src="https://devopsi.pl/wp-content/uploads/2021/02/Keycloak-1024x683.png" width="30" height="30"/> &nbsp;&nbsp;**Keycloak**
- <img src="https://www.vectorlogo.zone/logos/postgresql/postgresql-icon.svg" width="30" height="30"/> &nbsp;&nbsp;**PostgreSQL**
- <img src="https://upload.wikimedia.org/wikipedia/commons/2/29/Postgresql_elephant.svg" width="30" height="30"/> &nbsp;&nbsp;**PGAdmin**
- <img src="https://resources.jetbrains.com/storage/products/company/brand/logos/IntelliJ_IDEA_icon.svg" width="30" height="30"/> &nbsp;&nbsp;**IntelliJ IDEA**
- <img src="https://git-scm.com/images/logos/downloads/Git-Icon-1788C.png" width="30" height="30"/> &nbsp;&nbsp;**Git**
- <img src="https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png" width="30" height="30"/> &nbsp;&nbsp;**GitHub**

## Architecture de l'Application

![Architecture Diagram](placeholder-architecture-image.png)

L'architecture suit le pattern microservices avec les composants suivants :
- **Gateway Service** : Point d'entrée unique pour toutes les requêtes
- **Discovery Service** : Service de découverte et d'enregistrement des microservices
- **Config Service** : Gestion centralisée de la configuration
- **Microservices métiers** : Customer Service, Inventory Service, Billing Service
- **Frontend Angular** : Interface utilisateur moderne et réactive
- **Keycloak** : Serveur d'authentification et d'autorisation
- **PostgreSQL** : Base de données relationnelle

## Diagramme de Déploiement

![Deployment Diagram](placeholder-deployment-diagram.png)

Ce diagramme illustre le déploiement des différents services et leurs interactions au sein de l'infrastructure.

## Architecture Microservices

### Gateway Service

Le Gateway Service agit comme un point d'entrée unique (API Gateway) pour l'ensemble de l'application. Il route les requêtes vers les microservices appropriés et gère l'authentification avec Keycloak.

**Utilité** :
- Routage intelligent des requêtes
- Load balancing
- Sécurisation des endpoints avec Keycloak
- Filtres personnalisés (logging, rate limiting)

**Configuration** :

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: customer-service
          uri: lb://CUSTOMER-SERVICE
          predicates:
            - Path=/api/customers/**
        - id: inventory-service
          uri: lb://INVENTORY-SERVICE
          predicates:
            - Path=/api/inventory/**
```

**Dépendances principales** :
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

### Discovery Service (Eureka Server)

Le Discovery Service utilise Netflix Eureka pour permettre aux microservices de s'enregistrer et de se découvrir dynamiquement. Il facilite la communication inter-services sans configuration statique.

**Utilité** :
- Enregistrement automatique des services
- Découverte de services (Service Discovery)
- Health checking des instances
- Load balancing côté client

**Configuration** :

```java
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }
}
```

```yaml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

### Config Service

Le Config Service centralise la gestion des configurations pour tous les microservices. Il utilise Spring Cloud Config Server pour externaliser les propriétés de configuration.

**Utilité** :
- Configuration centralisée
- Gestion des profils (dev, prod, test)
- Rafraîchissement dynamique de la configuration
- Sécurisation des propriétés sensibles

**Configuration** :

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApplication.class, args);
    }
}
```

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-repo/config-repo
          default-label: main
```

### Customer Service

Le Customer Service gère toutes les opérations relatives aux clients (customers). Ce microservice expose des API REST pour la création, modification, consultation et suppression des données clients.

**Fonctionnalités** :
- CRUD complet sur les entités Customer
- Validation des données
- Pagination et recherche
- Intégration avec la base de données PostgreSQL

**Entité Customer** :

```java
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phone;
}
```

**Controller** :

```java
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.findAll();
    }
    
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.save(customer);
    }
}
```

### Inventory Service

L'Inventory Service gère l'inventaire des produits. Il maintient les informations sur les stocks, les produits disponibles et leurs quantités.

**Fonctionnalités** :
- Gestion des produits (Product)
- Suivi des stocks
- Vérification de disponibilité
- Mise à jour des quantités

**Entité Product** :

```java
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
}
```

### Billing Service

Le Billing Service gère la facturation et les opérations financières. Il communique avec Customer Service et Inventory Service via OpenFeign pour récupérer les informations nécessaires.

**Fonctionnalités** :
- Création de factures (Invoice)
- Gestion des lignes de facturation
- Calcul des totaux
- Communication inter-services avec OpenFeign

**Entities** :

```java
@Entity
@Data
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private Double amount;
    private Long customerId;
    
    @OneToMany(mappedBy = "invoice")
    private List<InvoiceItem> items;
}
```

**OpenFeign Client** :

```java
@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerRestClient {
    @GetMapping("/api/customers/{id}")
    Customer getCustomerById(@PathVariable Long id);
}
```

## Application Angular

### Introduction

Angular est un framework JavaScript/TypeScript développé par Google pour la création d'applications web single-page (SPA). Il offre une architecture basée sur les composants, le data binding bidirectionnel, l'injection de dépendances et un écosystème riche d'outils.

**Configuration** :

L'application Angular communique avec le backend via le Gateway Service et utilise Keycloak pour l'authentification. La configuration se fait dans le fichier `environment.ts` :

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8888',
  keycloakUrl: 'http://localhost:8080',
  realm: 'your-realm',
  clientId: 'angular-client'
};
```

### Création d'une Application Angular

Pour créer une nouvelle application Angular :

```bash
# Installer Angular CLI
npm install -g @angular/cli

# Créer un nouveau projet
ng new k8-frontend
cd k8-frontend

# Lancer l'application
ng serve
```

### AppModule Configuration

Le module principal de l'application configure les dépendances nécessaires, notamment pour l'intégration avec Keycloak :

```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:8080',
        realm: 'your-realm',
        clientId: 'angular-client'
      },
      initOptions: {
        onLoad: 'login-required',
        checkLoginIframe: false
      }
    });
}

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    KeycloakAngularModule
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

### Création de Services Angular

Les services Angular encapsulent la logique métier et les appels HTTP :

```bash
# Créer un service Customer
ng generate service services/customer

# Créer un service Product
ng generate service services/product
```

**Exemple de service** :

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private apiUrl = `${environment.apiUrl}/api/customers`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  create(customer: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, customer);
  }
}
```

### Création de Composants Angular

Les composants représentent les vues de l'application :

```bash
# Créer un composant Customers
ng generate component components/customers

# Créer un composant Products
ng generate component components/products

# Créer un composant Invoices
ng generate component components/invoices
```

**Exemple de composant** :

```typescript
import { Component, OnInit } from '@angular/core';
import { CustomerService } from '../../services/customer.service';

@Component({
  selector: 'app-customers',
  templateUrl: './customers.component.html',
  styleUrls: ['./customers.component.css']
})
export class CustomersComponent implements OnInit {
  customers: any[] = [];

  constructor(private customerService: CustomerService) { }

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.customerService.getAll().subscribe(
      data => {
        this.customers = data;
      },
      error => {
        console.error('Error loading customers', error);
      }
    );
  }
}
```

## Keycloak - Sécurité et Authentification

Keycloak est une solution open-source d'Identity and Access Management (IAM) qui fournit l'authentification et l'autorisation pour les applications. Il supporte les protocoles standards comme OAuth 2.0, OpenID Connect et SAML 2.0.

**Fonctionnalités** :
- Single Sign-On (SSO)
- Gestion des utilisateurs et des rôles
- Fédération d'identité (LDAP, Active Directory)
- Social Login (Google, Facebook, etc.)
- Protection des API avec JWT

**Configuration dans Spring Boot** :

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/your-realm
          jwk-set-uri: http://localhost:8080/realms/your-realm/protocol/openid-connect/certs
```

**Configuration de sécurité** :

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
            );
        return http.build();
    }
}
```

## PostgreSQL et PGAdmin

### PostgreSQL

PostgreSQL est un système de gestion de base de données relationnelle open-source, robuste et performant. Il est utilisé pour persister les données des différents microservices.

**Configuration dans application.yml** :

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/customerdb
    username: postgres
    password: your-password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
    show-sql: true
```

### PGAdmin

PGAdmin est une interface graphique web pour gérer PostgreSQL. Il permet de visualiser les bases de données, exécuter des requêtes SQL et administrer les serveurs PostgreSQL.

**Accès** : http://localhost:5050

## Installation et Démarrage

### Prérequis

- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Keycloak 22+
- Maven 3.8+

### Démarrage des Services

1. **Démarrer PostgreSQL et PGAdmin**
```bash
docker-compose up -d postgres pgadmin
```

2. **Démarrer Keycloak**
```bash
docker-compose up -d keycloak
```

3. **Démarrer les microservices dans l'ordre**
```bash
# Config Service
cd config-service
mvn spring-boot:run

# Discovery Service
cd discovery-service
mvn spring-boot:run

# Gateway Service
cd gateway-service
mvn spring-boot:run

# Microservices métiers
cd customer-service && mvn spring-boot:run
cd inventory-service && mvn spring-boot:run
cd billing-service && mvn spring-boot:run
```

4. **Démarrer l'application Angular**
```bash
cd k8-frontend
npm install
ng serve
```

### Accès aux Services

- **Angular Frontend** : http://localhost:4200
- **Gateway API** : http://localhost:8888
- **Discovery Service** : http://localhost:8761
- **Keycloak Admin** : http://localhost:8080
- **PGAdmin** : http://localhost:5050

## Captures d'Écran


---

**Auteur** : Abdelmonaim AHDOUD  
**Date** : Novembre 2025  
**Version** : 1.0.0
