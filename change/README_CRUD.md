# Système CRUD Générique Refactorisé - Architecture POO

## 📋 Description

Système CRUD (Create, Read, Update, Delete) **100% POO et factorisé** où :
- ✅ **Les entités héritent directement de `GenericDAO`** (plus besoin de créer des DAO séparés)
- ✅ **Toute la logique métier est dans les modèles**
- ✅ **Structure des fonctions : Contrôle → Métier → Persistance**
- ✅ **Conversion automatique camelCase ↔ snake_case**

## 🏗️ Architecture POO

```
┌─────────────────────────────────────┐
│   User extends GenericDAO<User>     │ ← Entité avec CRUD + Logique métier
│   - validate()                      │
│   - beforeCreate()                  │
│   - getFullName()                   │
│   - isAdult()                       │
└──────────────┬──────────────────────┘
               │ hérite de
               ▼
┌─────────────────────────────────────┐
│   GenericDAO<T>                     │ ← Classe abstraite générique
│   - create()                        │
│   - getById()                       │
│   - update()                        │
│   - delete()                        │
│   - getAll()                        │
└──────────────┬──────────────────────┘
               │ utilise
               ▼
┌─────────────────────────────────────┐
│   DB (connexion PostgreSQL)         │
└─────────────────────────────────────┘
```

## 📦 Structure des packages

```
mg.itu.database
├── dao/                    # Couche persistance générique
│   ├── Entity.java              (annotation @Entity)
│   ├── NamingUtils.java         (camelCase ↔ snake_case)
│   ├── GenericDAO.java          (CRUD générique abstrait)
│   └── DAOExample.java          (exemples d'utilisation)
│
├── model/                  # Entités (héritent de GenericDAO)
│   ├── User.java                (utilisateurs)
│   ├── CodeStatutDevise.java    (codes statuts)
│   ├── Devise.java              (devises avec cours)
│   └── ActionRole.java          (permissions)
│
├── service/                # Services (logique applicative)
│   ├── UserService.java
│   ├── CodeStatutDeviseService.java
│   ├── DeviseService.java
│   └── ActionRoleService.java
│
└── utils/                  # Utilitaires
    └── DB.java                  (connexion base de données)
```

## ✨ Fonctionnalités principales

### 1. Les entités héritent de GenericDAO

**Avant** (ancien système - répétitif) :
```java
// Il fallait créer un DAO par entité
public class UserDAO extends GenericDAO<User> {
    public UserDAO() { super(User.class); }
}
```

**Maintenant** (refactorisé - POO) :
```java
// L'entité hérite directement de GenericDAO !
@Entity(tableName = "users")
public class User extends GenericDAO<User> {
    private int id;
    private String firstName;
    private String email;
    // ... getters/setters + logique métier
}
```

### 2. Structure des fonctions : Contrôle → Métier → Persistance

Toutes les opérations CRUD suivent cette structure :

```java
public T create() throws SQLException {
    // 1. CONTRÔLE - Validation des données
    this.validate();
    
    // 2. MÉTIER - Logique avant sauvegarde
    this.beforeCreate();
    
    // 3. PERSISTANCE - Insertion en base
    // ... requête SQL ...
}
```

### 3. Logique métier dans les modèles

**Exemple avec User :**
```java
@Entity(tableName = "users")
public class User extends GenericDAO<User> {
    private String firstName;
    private String email;
    private int age;
    
    // SETTERS avec validation simple
    public void setAge(int age) {
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Âge invalide");
        }
        this.age = age;
    }
    
    // VALIDATION COMPLEXE (appelée avant create/update)
    @Override
    protected void validate() throws SQLException {
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("Prénom obligatoire");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email invalide");
        }
    }
    
    // NORMALISATION (appelée avant create/update)
    @Override
    protected void beforeCreate() throws SQLException {
        firstName = firstName.trim();
        email = email.trim().toLowerCase();
    }
    
    // MÉTHODES MÉTIER
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean isAdult() {
        return age >= 18;
    }
    
    public String getAgeCategory() {
        if (age < 18) return "Mineur";
        if (age < 65) return "Adulte";
        return "Senior";
    }
}
```

## 🚀 Utilisation

### Créer une entité

```java
// 1. Créer l'objet
User user = new User("Jean", "Dupont", "jean@email.com", 25);

// 2. Le sauvegarder (validation automatique)
User created = user.create();

System.out.println("ID généré : " + created.getId());
```

### Lire une entité

```java
// Récupérer par ID (méthode statique)
User user = User.getById(User.class, 1);

// Utiliser les méthodes métier
System.out.println(user.getFullName());
System.out.println("Majeur : " + user.isAdult());
```

### Mettre à jour

```java
// Modifier l'objet
user.setAge(26);
user.setEmail("nouveau@email.com");

// Sauvegarder (validation + normalisation automatiques)
User updated = user.update();
```

### Supprimer

```java
// Méthode 1 : depuis l'objet
user.delete();

// Méthode 2 : par ID
User.deleteById(User.class, 1);
```

### Récupérer tous avec filtres

```java
// Sans filtre
List<User> all = User.getAll(User.class);

// Avec filtres
Map<String, Object> filters = new HashMap<>();
filters.put("age", 25);
filters.put("firstName", "Jean");

List<User> filtered = User.getAll(User.class, filters, "lastName", 10, 0);
//                                               ↑        ↑          ↑   ↑
//                                           filters  orderBy   limit offset
```

## 🗂️ Entités disponibles

### 1. User (Utilisateurs)

```java
@Entity(tableName = "users")
public class User extends GenericDAO<User> {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private int age;
    
    // Méthodes métier
    public String getFullName()
    public boolean isAdult()
    public String getAgeCategory()
}
```

### 2. CodeStatutDevise (Codes statuts)

```java
@Entity(tableName = "codes_statuts_devises")
public class CodeStatutDevise extends GenericDAO<CodeStatutDevise> {
    private int id;
    private String libelle;  // Normalisé en MAJUSCULES
}
```

### 3. Devise (Devises)

```java
@Entity(tableName = "devises")
public class Devise extends GenericDAO<Devise> {
    private int id;
    private String code;           // Code ISO (EUR, USD, etc.)
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal cours;
    private Integer statutCode;
    
    // Méthodes métier
    public boolean isActiveAt(LocalDate date)
    public boolean isCurrentlyActive()
    public BigDecimal convertTo(BigDecimal montant, Devise autreDevise)
    public long getDaysOfValidity()
}
```

**Exemple d'utilisation :**
```java
// Créer des devises
Devise eur = new Devise("EUR", LocalDate.now(), new BigDecimal("1.00"), 1);
Devise usd = new Devise("USD", LocalDate.now(), new BigDecimal("1.18"), 1);

eur = eur.create();
usd = usd.create();

// Conversion
BigDecimal montantEUR = new BigDecimal("100.00");
BigDecimal montantUSD = eur.convertTo(montantEUR, usd);
System.out.println("100 EUR = " + montantUSD + " USD");

// Vérifier si active
System.out.println("EUR active : " + eur.isCurrentlyActive());
```

### 4. ActionRole (Permissions)

```java
@Entity(tableName = "actions_roles")
public class ActionRole extends GenericDAO<ActionRole> {
    private int id;
    private String nomTable;     // Nom de la table
    private String nomAction;    // CREATE, READ, UPDATE, DELETE
    private int roleMinimum;     // 1=admin, 2=manager, 3=user...
    
    // Méthodes métier
    public boolean hasPermission(int userRole)
    public String getRoleMinimumName()
    public boolean isStandardCrudAction()
    public String getPermissionDescription()
}
```

**Exemple d'utilisation :**
```java
// Créer une permission
ActionRole perm = new ActionRole("users", "DELETE", 1);
perm = perm.create();

// Vérifier les permissions
System.out.println("Admin (1) peut DELETE : " + perm.hasPermission(1));  // true
System.out.println("User (3) peut DELETE : " + perm.hasPermission(3));   // false

System.out.println(perm.getPermissionDescription());
// → "Action 'DELETE' sur la table 'users' requiert le rôle ADMIN (niveau 1)"
```

## 🔄 Hooks disponibles

Les méthodes suivantes peuvent être surchargées dans vos entités :

```java
protected void validate()       // Validation complexe
protected void beforeCreate()   // Avant insertion
protected void afterCreate()    // Après insertion
protected void beforeUpdate()   // Avant mise à jour
protected void afterUpdate()    // Après mise à jour
protected void beforeDelete()   // Avant suppression
protected void afterDelete()    // Après suppression
```

## 🗃️ Scripts SQL

```sql
-- Table users
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    age INTEGER CHECK (age >= 0 AND age <= 150)
);

-- Table codes_statuts_devises
CREATE TABLE codes_statuts_devises (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(20) NOT NULL
);

-- Table devises
CREATE TABLE devises (
    id SERIAL PRIMARY KEY,
    code VARCHAR(3) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE,
    cours DECIMAL(15, 2) NOT NULL DEFAULT 0,
    statut_code INTEGER REFERENCES codes_statuts_devises(id)
);

-- Table actions_roles
CREATE TABLE actions_roles (
    id SERIAL PRIMARY KEY,
    nom_table VARCHAR(100) NOT NULL,
    nom_action VARCHAR(100) NOT NULL,
    role_minimum INTEGER NOT NULL DEFAULT 1
);
```

## ✅ Avantages de cette architecture

| Avant (ancien) | Maintenant (refactorisé) |
|----------------|--------------------------|
| ❌ Créer un DAO par entité | ✅ Entité hérite de GenericDAO |
| ❌ Validation dans le Service | ✅ Validation dans le modèle (setters + validate()) |
| ❌ Logique métier éparpillée | ✅ Logique métier dans le modèle |
| ❌ Code répétitif | ✅ Code factorisé et POO |
| ❌ Structure peu claire | ✅ Structure : Contrôle → Métier → Persistance |

## 🎯 Pour créer une nouvelle entité

```java
// 1. Créer le modèle (hérite de GenericDAO)
@Entity(tableName = "produits")
public class Produit extends GenericDAO<Produit> {
    private int id;
    private String nom;
    private BigDecimal prix;
    
    // Constructeur par défaut OBLIGATOIRE
    public Produit() {}
    
    // Getters/Setters avec validation simple
    public void setPrix(BigDecimal prix) {
        if (prix.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Prix négatif");
        }
        this.prix = prix;
    }
    
    // Validation complexe
    @Override
    protected void validate() throws SQLException {
        if (nom == null || nom.isEmpty()) {
            throw new IllegalArgumentException("Nom obligatoire");
        }
        if (prix == null || prix.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Prix invalide");
        }
    }
    
    // Normalisation
    @Override
    protected void beforeCreate() throws SQLException {
        nom = nom.trim().toUpperCase();
        prix = prix.setScale(2, RoundingMode.HALF_UP);
    }
    
    // Méthodes métier
    public BigDecimal getPrixTTC() {
        return prix.multiply(new BigDecimal("1.20")); // +20% TVA
    }
}

// 2. Utiliser directement !
Produit p = new Produit();
p.setNom("Ordinateur");
p.setPrix(new BigDecimal("999.99"));
Produit created = p.create();

System.out.println("Prix TTC : " + created.getPrixTTC());
```

## 🔧 Configuration

Modifier `DB.java` pour votre base de données :

```java
connection = DriverManager.getConnection(
    "jdbc:postgresql://localhost:5432/banque_change",
    "postgres",
    "password"
);
```

## 📝 Exemple complet

Exécuter `DAOExample.java` pour voir toutes les démonstrations !

---

**🎉 Architecture 100% POO, factorisée et respectant les principes SOLID !**
