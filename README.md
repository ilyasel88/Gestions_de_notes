# 🎓 Gestion des Notes — Académie

Application de bureau JavaFX pour la gestion des notes étudiantes, avec interface dark premium et persistance MySQL.

---

## Aperçu

Ce projet est un système complet de gestion académique permettant de gérer les étudiants, enseignants, modules, sous-modules et notes d'une filière. Il intègre un système de rôles, un moteur de validation d'année automatique, et des statistiques visuelles.

---

## Fonctionnalités

## Rôles utilisateurs
| Rôle | Accès |
|---|---|
| `PLANNING` | Gestion complète : étudiants, promotions, modules, enseignants |
| `ENSEIGNANT` | Saisie des notes pour ses propres sous-modules |
| `ETUDIANT` | Consultation de ses notes et résultats |
| `RESPONSABLE_FILIERE` | Suivi de la filière, validation d'année, statistiques |

### Modules fonctionnels
- **Gestion des étudiants** — ajout, modification, archivage, fiche détaillée
- **Gestion des enseignants** — affectation aux sous-modules, création de compte automatique
- **Gestion des modules & sous-modules** — coefficients, promotions associées
- **Saisie des notes** — note initiale + rattrapage, import Excel (Apache POI)
- **Validation d'année** — calcul automatique selon le règlement (moyenne ≥ 11, ≤ 2 modules non validés, aucun module < 6)
- **Statistiques** — graphiques en barres et camembert (BarChart / PieChart JavaFX)
- **Export** — export des résultats vers Excel

---

## Technologies

- **Java 11+**
- **JavaFX** — interface graphique (FXML-less, code pur)
- **MySQL** — base de données relationnelle via JDBC (`mysql-connector-java`)
- **Apache POI** — import/export de fichiers Excel (`.xlsx`)
- **CSS JavaFX** — thème dark premium personnalisé (`style.css`)

---

## Prérequis

- JDK 11 ou supérieur
- MySQL Server (local, port `3306`)
- JavaFX SDK
- Dépendances Maven/Gradle : `mysql-connector-java`, `apache-poi`

---

## Installation & Lancement

### 1. Configurer la base de données

Créer une base de données MySQL vide :

```sql
CREATE DATABASE gestion_notes;
```

Les tables sont créées automatiquement au premier lancement via `initialiserTables()` dans `StockageMySQL`.

### 2. Configurer la connexion

Dans `StockageMySQL.java`, modifier si nécessaire :

```java
String url      = "jdbc:mysql://localhost:3306/gestion_notes?useSSL=false&serverTimezone=UTC";
String user     = "root";
String password = "root";
```

### 3. Initialiser la base (optionnel)

```bash
javac InitDB.java StockageMySQL.java ...
java InitDB
```

### 4. Lancer l'application

```bash
javac *.java
java Principal
```

Ou via votre IDE (IntelliJ, Eclipse, VS Code) en configurant le module JavaFX.

---

## Structure du projet

```
├── Principal.java            # Point d'entrée
├── InterfaceFx.java          # Interface graphique complète (JavaFX)
├── StockageMySQL.java        # Couche d'accès aux données (JDBC)
├── ReglementEvaluation.java  # Moteur de calcul et validation d'année
├── Utilisateur.java          # Modèle utilisateur (authentification)
├── Etudiant.java             # Modèle étudiant
├── Enseignant.java           # Modèle enseignant
├── Promotion.java            # Modèle promotion / filière
├── Module.java               # Modèle module pédagogique
├── SousModule.java           # Modèle sous-module (avec coefficient)
├── Note.java                 # Modèle note (initiale + rattrapage)
├── InitDB.java               # Initialisation de la base de données
└── style.css                 # Thème dark premium (JavaFX CSS)
```

---

## Règlement de validation

Une année est validée si toutes les conditions suivantes sont remplies :

- Moyenne générale **≥ 11/20**
- Nombre de modules non validés **≤ 2**
- Aucun module avec une moyenne **< 6/20**

Un module est validé si :

- Moyenne du module **≥ 11/20**
- Aucune note dans ce module **< 6/20**

La note définitive d'un sous-module est `max(note, note_rattrapage)`.

---

## Schéma de la base de données

```
utilisateur       (id, nom, mot_de_passe, role, filiere)
promotion         (id, nom, filiere, annee_academique)
etudiant          (id, prenom, nom, num_etudiant, archive, promotion_id, date_naissance, telephone, email)
enseignant        (id, nom)
module            (id, nom, description)
sous_module       (id, nom, coefficient, module_id, promotion_id, enseignant_id)
note              (etudiant_id, sous_module_id, valeur, valeur_rattrapage)
validation_promotion (etudiant_id, promotion_id, valide)
```

---

## Compte par défaut

Au premier lancement, créer manuellement un utilisateur `PLANNING` en base :

```sql
INSERT INTO utilisateur (nom, mot_de_passe, role, filiere)
VALUES ('admin', 'admin', 'PLANNING', NULL);
```

> ⚠️ Les mots de passe sont stockés en clair. Pour un usage en production, intégrer un hachage (ex. BCrypt).

---

## Licence

Projet académique — usage libre pour des fins éducatives.
