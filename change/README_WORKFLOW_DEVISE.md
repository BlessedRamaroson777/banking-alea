# Workflow de Validation des Devises

## Vue d'ensemble

Le système de gestion des devises implémente un workflow de validation en 3 états :
1. **En attente** (statut_code = 1) - État initial lors de la création
2. **Valide** (statut_code = 2) - Après validation
3. **Refusé** (statut_code = 3) - Si la création est refusée

## Fonctionnement

### 1. Création d'une devise

Lors de la création d'une devise via `POST /api/devises`, le statut est automatiquement défini à **"En attente"** (id=1).

```json
POST /api/devises
Content-Type: application/json

{
  "code": "JPY",
  "dateDebut": "2024-01-01",
  "cours": 150.50
}
```

**Réponse :**
```json
{
  "id": 4,
  "code": "JPY",
  "dateDebut": "2024-01-01",
  "dateFin": null,
  "cours": 150.50,
  "statutCode": 1
}
```

### 2. Consultation des devises en attente

Pour voir toutes les devises en attente de validation :

```
GET /api/devises/en-attente
```

**Réponse :**
```json
[
  {
    "id": 4,
    "code": "JPY",
    "dateDebut": "2024-01-01",
    "cours": 150.50,
    "statutCode": 1
  }
]
```

### 3. Validation d'une devise

Pour valider une devise en attente (passer le statut de 1 à 2) :

```
POST /api/devises/{id}/valider
```

**Exemple :**
```
POST /api/devises/4/valider
```

**Réponse :**
```json
{
  "id": 4,
  "code": "JPY",
  "dateDebut": "2024-01-01",
  "dateFin": null,
  "cours": 150.50,
  "statutCode": 2
}
```

### 4. Refus d'une devise

Pour refuser une devise en attente (passer le statut de 1 à 3) :

```
POST /api/devises/{id}/refuser
```

**Exemple :**
```
POST /api/devises/4/refuser
```

**Réponse :**
```json
{
  "id": 4,
  "code": "JPY",
  "dateDebut": "2024-01-01",
  "dateFin": null,
  "cours": 150.50,
  "statutCode": 3
}
```

## Méthodes Java disponibles

### Dans la classe `Devise`

```java
// Vérifier l'état
boolean isEnAttente = devise.isEnAttente();    // statutCode == 1
boolean isValidee = devise.isValidee();        // statutCode == 2
boolean isRefusee = devise.isRefusee();        // statutCode == 3

// Valider une devise
Devise deviseValidee = devise.validerCreation();

// Refuser une devise
Devise deviseRefusee = devise.refuserCreation();
```

## Règles de validation

1. **Création automatique** : Toute nouvelle devise est créée avec le statut "En attente"
2. **Transition unique** : Seules les devises "En attente" peuvent être validées ou refusées
3. **Exception levée** : Une `IllegalStateException` est levée si on essaie de valider/refuser une devise qui n'est pas en attente

## Endpoints REST disponibles

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/devises` | Créer une devise (statut = "En attente") |
| GET | `/api/devises/{id}` | Récupérer une devise par son ID |
| GET | `/api/devises` | Lister toutes les devises |
| GET | `/api/devises/en-attente` | Lister les devises en attente |
| POST | `/api/devises/{id}/valider` | Valider une devise |
| POST | `/api/devises/{id}/refuser` | Refuser une devise |
| PUT | `/api/devises/{id}` | Mettre à jour une devise |
| DELETE | `/api/devises/{id}` | Supprimer une devise |

## Codes d'état HTTP

- **200 OK** : Opération réussie
- **201 CREATED** : Devise créée avec succès
- **400 BAD REQUEST** : Données invalides ou devise pas en attente
- **404 NOT FOUND** : Devise non trouvée
- **500 INTERNAL SERVER ERROR** : Erreur serveur

## Exemple de workflow complet

```bash
# 1. Créer une devise (statut = 1 automatiquement)
curl -X POST http://localhost:8080/change/api/devises \
  -H "Content-Type: application/json" \
  -d '{"code":"GBP","dateDebut":"2024-01-01","cours":0.75}'

# 2. Lister les devises en attente
curl http://localhost:8080/change/api/devises/en-attente

# 3. Valider la devise (ID 5 par exemple)
curl -X POST http://localhost:8080/change/api/devises/5/valider

# 4. Vérifier la mise à jour
curl http://localhost:8080/change/api/devises/5
```

## Modifications du code

### Classe `Devise.java`

- **Méthode `beforeCreate()`** : Définit automatiquement `statutCode = 1` si non spécifié
- **Méthode `validerCreation()`** : Change le statut à 2 (Valide)
- **Méthode `refuserCreation()`** : Change le statut à 3 (Refusé)
- **Méthodes helper** : `isEnAttente()`, `isValidee()`, `isRefusee()`

### Classe `DeviseRestController.java`

- **Endpoint `/valider`** : Valide une devise
- **Endpoint `/refuser`** : Refuse une devise
- **Endpoint `/en-attente`** : Liste les devises en attente

### Classe `GenericDAO.java`

- **Vérification des colonnes** : Les noms de colonnes sont maintenant vérifiés dans `create()` et `update()`
- **Logs de débogage** : Affichage des requêtes SQL générées pour faciliter le débogage
