# Système de Validation des Modifications de Devises

## Architecture POO

Le système respecte les principes de la Programmation Orientée Objet :

### Encapsulation
- **Validation dans les setters** : Chaque setter de `DeviseModification` effectue des contrôles simples
- **Logique métier dans le modèle** : La méthode `proposeModification()` est dans `DeviseModification`
- **Délégation de validation** : `DeviseModification.validate()` utilise `Devise.validate()` pour garantir la cohérence

### Responsabilités

#### Modèle `DeviseModification`
- **Validation simple** : Setters avec contrôles basiques (format, valeurs positives)
- **Validation complexe** : `validate()` crée une `Devise` temporaire et délègue la validation
- **Logique métier** : `proposeModification()`, `validerModification()`, `refuserModification()`

#### Contrôleur REST `DeviseRestController`
- **Délégation** : Appelle `DeviseModification.proposeModification(id, data)`
- **Gestion des erreurs** : Retourne les codes HTTP appropriés
- **Pas de logique métier** : Seulement orchestration

#### Service `DeviseModificationService`
- **Couche d'abstraction** : Entre le contrôleur et le modèle
- **Transactions** : Peut gérer les transactions complexes si nécessaire

#### EJB `DeviseModificationEJB`
- **Exposition distante** : Interface Remote pour accès EJB
- **Délégation** : Utilise le service sous-jacent

## Structure de la Table `devises_modifications`

```sql
CREATE TABLE devises_modifications (
    id                SERIAL PRIMARY KEY,
    devise_id         INTEGER NOT NULL REFERENCES devises(id) ON DELETE CASCADE,
    code              VARCHAR(3),
    date_debut        DATE,
    date_fin          DATE,
    cours             DECIMAL(15, 2),
    statut_code       INTEGER REFERENCES codes_statuts_devises(id),
    statut_validation INTEGER NOT NULL DEFAULT 1,
    date_proposition  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_traitement   TIMESTAMP
);
```

## Workflow de Modification

### 1. Proposer une modification

```bash
POST /api/devises/{id}/propose-modification
Content-Type: application/json

{
  "code": "JPY",
  "cours": 155.75,
  "dateDebut": "2024-01-01"
}
```

**Traitement POO :**
1. Le contrôleur reçoit un objet `DeviseModification`
2. Appelle la méthode statique `DeviseModification.proposeModification(id, data)`
3. La méthode :
   - Récupère la devise originale
   - Compare les champs et garde uniquement ceux qui changent
   - Crée une nouvelle `DeviseModification`
   - Appelle `create()` qui déclenche `validate()`
4. `validate()` :
   - Crée une `Devise` temporaire avec les nouvelles valeurs
   - Appelle `devise.validate()` pour vérifier la cohérence globale
   - Lève une exception si invalide

**Réponse :**
```json
{
  "id": 1,
  "deviseId": 4,
  "code": "JPY",
  "cours": 155.75,
  "dateDebut": "2024-01-01",
  "statutValidation": 1,
  "dateProposition": "2024-11-04T08:00:00"
}
```

### 2. Consulter les modifications en attente

```bash
GET /api/devises-modifications/en-attente
```

### 3. Valider une modification

```bash
POST /api/devises-modifications/{id}/valider
```

**Traitement :**
- Récupère la `DeviseModification`
- Applique les changements à la `Devise` originale
- Met à jour `statutValidation = 2` (Validé)
- Enregistre `dateTraitement`

### 4. Refuser une modification

```bash
POST /api/devises-modifications/{id}/refuser
```

## Validation en Cascade

```
DeviseModification.validate()
    ↓
Récupère Devise originale
    ↓
Crée Devise temporaire avec modifications
    ↓
Appelle Devise.validate()
    ↓
Vérifie :
    - Code ISO (3 caractères)
    - Cours positif
    - Date début obligatoire
    - Date fin après date début
    - Cours max 15 chiffres
```

## Endpoints REST disponibles

### Devises
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/devises` | Créer une devise (statut = "En attente") |
| GET | `/api/devises/{id}` | Récupérer une devise |
| POST | `/api/devises/{id}/valider` | Valider une devise |
| POST | `/api/devises/{id}/propose-modification` | **Proposer une modification** |

### Modifications de Devises
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/devises-modifications` | Créer une proposition |
| GET | `/api/devises-modifications/{id}` | Récupérer une proposition |
| GET | `/api/devises-modifications` | Lister toutes les propositions |
| GET | `/api/devises-modifications/en-attente` | Lister les propositions en attente |
| GET | `/api/devises-modifications/devise/{deviseId}` | Propositions pour une devise |
| POST | `/api/devises-modifications/{id}/valider` | **Valider et appliquer** |
| POST | `/api/devises-modifications/{id}/refuser` | **Refuser** |
| DELETE | `/api/devises-modifications/{id}` | Supprimer une proposition |

## EJB Remote

### JNDI Lookup
```java
// DeviseModificationEJB
Context ctx = new InitialContext();
DeviseModificationRemote remote = (DeviseModificationRemote) ctx.lookup(
    "java:global/change/DeviseModificationEJB!mg.itu.ejb.remote.DeviseModificationRemote"
);

// Proposer une modification
DeviseModification modif = new DeviseModification();
modif.setCode("GBP");
modif.setCours(new BigDecimal("0.85"));

DeviseModification result = remote.proposeModification(5, modif);
```

## Exemple Complet avec Validation POO

```java
// 1. Créer une proposition (validation automatique)
DeviseModification proposition = new DeviseModification();
proposition.setCode("USD"); // Setter valide le format (3 chars)
proposition.setCours(new BigDecimal("1.15")); // Setter vérifie > 0

// 2. Proposer la modification
DeviseModification created = DeviseModification.proposeModification(4, proposition);
// Appelle validate() qui :
//   - Récupère la devise originale
//   - Crée une Devise test avec les nouvelles valeurs
//   - Appelle Devise.validate() pour vérifier la cohérence

// 3. Valider la modification
DeviseModification validee = created.validerModification();
// Applique les changements à la devise originale
```

## Principes POO Respectés

### 1. **Encapsulation**
- Les setters valident les données
- Les attributs sont privés
- Accès contrôlé via getters/setters

### 2. **Cohésion**
- Chaque classe a une responsabilité claire
- `DeviseModification` gère les propositions
- `Devise` gère les devises
- Controllers gèrent uniquement HTTP

### 3. **Couplage faible**
- `DeviseModification` utilise `Devise.validate()` sans dupliquer le code
- Controllers délèguent au modèle
- Services abstraient la persistance

### 4. **Réutilisation**
- La validation de `Devise` est réutilisée
- Pas de duplication de logique métier
- Méthode statique `proposeModification()` encapsule la logique

### 5. **Responsabilité unique (SRP)**
- **Modèle** : Logique métier et validation
- **Service** : Orchestration des opérations
- **Controller** : Gestion HTTP
- **EJB** : Exposition distante

## Avantages de cette Architecture

1. **Maintenabilité** : Logique centralisée dans le modèle
2. **Testabilité** : Chaque couche peut être testée indépendamment
3. **Évolutivité** : Facile d'ajouter de nouvelles validations
4. **Cohérence** : Une seule source de vérité pour les règles métier
5. **Sécurité** : Validation à plusieurs niveaux (setter → validate → Devise.validate)
