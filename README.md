# 🛒 ÉléganceShop – Application Web E-Commerce avec Spring Boot

## 📌 Description

ÉléganceShop est une application web e-commerce développée avec **Spring Boot** permettant la gestion des utilisateurs, des articles, des commandes et d'un panier d'achat.

Le projet a été réalisé dans le cadre du module **JEE** en respectant une architecture en couches et les principes de faibles couplages entre les différentes couches de l'application.

---

## 🚀 Technologies Utilisées

| Technologie | Rôle |
|---|---|
| Java 17 | Langage principal |
| Spring Boot | Framework backend |
| Spring MVC | Gestion des requêtes HTTP |
| Spring Security | Authentification & autorisation |
| Spring Data JPA | Accès aux données |
| Thymeleaf | Moteur de templates |
| Bootstrap | Interface utilisateur |
| MySQL / H2 Database | Base de données |
| Maven | Gestion des dépendances |

---

## 🏗️ Architecture du Projet

L'application respecte une architecture en couches :

```
Présentation   →   Thymeleaf + Bootstrap
Controller     →   Gestion des requêtes HTTP
Service        →   Logique métier
Repository     →   Accès aux données (Spring Data JPA)
Base de données →  MySQL / H2
```

---

## 🔐 Fonctionnalités Principales

### 👤 Authentification

- Inscription utilisateur
- Connexion sécurisée
- Gestion des rôles `USER` / `ADMIN`
- Protection des routes avec Spring Security

### 📦 Gestion des Articles

- Ajouter un article
- Modifier un article
- Supprimer un article
- Recherche multicritère
- Pagination et tri

Chaque article contient :

| Champ | Description |
|---|---|
| Identifiant | ID unique |
| Description | Détail de l'article |
| Prix | Prix unitaire |
| Date d'expiration | Validité du produit |
| Quantité en stock | Disponibilité |

### 🛒 Gestion du Panier

- Ajouter un article au panier
- Modifier la quantité
- Supprimer un article
- Calcul automatique du total

### 📑 Gestion des Commandes

- Création des commandes
- Validation des commandes
- Historique des commandes
- Gestion des statuts

### 👨‍💼 Dashboard Administrateur

- Gestion des utilisateurs
- Gestion des articles
- Gestion des commandes
- Statistiques générales

---

## 🔒 Sécurité

L'application utilise **Spring Security** pour :

- L'authentification
- L'autorisation par rôles
- La protection des pages sécurisées
- Le chiffrement des mots de passe avec **BCrypt**

---

## 🎨 Interface Utilisateur

L'interface a été développée avec **Thymeleaf** et **Bootstrap**. Elle offre :

- Un design moderne
- Une navigation simple
- Une expérience utilisateur fluide

---

## ⚙️ Lancement du Projet

### 1️⃣ Cloner le projet

```bash
git clone <url-du-projet>
```

### 2️⃣ Ouvrir avec IntelliJ IDEA

### 3️⃣ Configurer la base de données

Modifier le fichier `application.properties` selon votre environnement.

### 4️⃣ Lancer l'application

Via Maven :

```bash
mvn spring-boot:run
```

Ou lancer directement la classe :

```java
Application.java
```

---

## 🌐 Accès Application

```
http://localhost:8080
```

---

## 📷 Captures d'écran

- Interface utilisateur moderne
- Dashboard administrateur
- Gestion des articles et commandes

---

## 🎯 Objectif Pédagogique

Ce projet nous a permis de :

- Comprendre **Spring Boot** et **Spring MVC**
- Utiliser **Spring Security**
- Manipuler **Spring Data JPA**
- Respecter une **architecture en couches**
- Développer une application web **sécurisée et maintenable**

---

## 👩‍💻 Auteur

> Projet réalisé dans le cadre du module **JEE – Semestre 2**.
