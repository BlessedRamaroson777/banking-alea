# API REST - Guide des Filtres Généralisés

## 📋 Vue d'ensemble

Les endpoints REST `/api/devises` et `/api/devises-modifications` supportent maintenant des **filtres généralisés** via les query parameters, permettant des requêtes complexes sans modifier le code.

## 🎯 Syntaxe des filtres

### Égalité simple
```
GET /api/devises?code=USD
GET /api/devises?statutCode=1
```
→ `WHERE code = 'USD'`
→ `WHERE statut_code = 1`

### Comparaisons numériques

| Suffixe | Opérateur | Exemple | SQL généré |
|---------|-----------|---------|------------|
| `_gt` | `>` (greater than) | `cours_gt=1.0` | `WHERE cours > 1.0` |
| `_lt` | `<` (less than) | `cours_lt=100.0` | `WHERE cours < 100.0` |
| `_gte` | `>=` (greater or equal) | `cours_gte=1.0` | `WHERE cours >= 1.0` |
| `_lte` | `<=` (less or equal) | `cours_lte=100.0` | `WHERE cours <= 100.0` |
| `_ne` | `!=` (not equal) | `statutCode_ne=0` | `WHERE statut_code != 0` |

### Recherche partielle (LIKE)
```
GET /api/devises?code_like=%US%
```
→ `WHERE code LIKE '%US%'`

### Tests NULL
```
GET /api/devises?dateFin_null=true
GET /api/devises?dateFin_null=false
```
→ `WHERE date_fin IS NULL`
→ `WHERE date_fin IS NOT NULL`

### Paramètres spéciaux
- `orderBy` : Tri des résultats
- `limit` : Nombre maximum de résultats
- `offset` : Pagination

## 🚀 Exemples d'utilisation

### 1. Devises avec cours supérieur à 1.0
```bash
curl "http://localhost:8080/change/api/devises?cours_gt=1.0"
```

### 2. Devises sans date de fin
```bash
curl "http://localhost:8080/change/api/devises?dateFin_null=true"
```

### 3. Recherche de devise contenant "US"
```bash
curl "http://localhost:8080/change/api/devises?code_like=%25US%25"
```
Note: `%25` = `%` encodé en URL

### 4. Modifications en attente pour une devise
```bash
curl "http://localhost:8080/change/api/devises-modifications?deviseId=1&statutValidation=1"
```

### 5. Combinaisons multiples
```bash
curl "http://localhost:8080/change/api/devises?cours_gte=0.5&cours_lte=2.0&dateFin_null=true&orderBy=code&limit=10"
```
→ Devises avec cours entre 0.5 et 2.0, sans date de fin, triées par code, max 10 résultats

### 6. Devises créées récemment (avec cours != 0)
```bash
curl "http://localhost:8080/change/api/devises?cours_ne=0&statutCode=1&orderBy=id&limit=5"
```

## ⚙️ Détection automatique des types

Le système détecte automatiquement le type de données :

| Valeur | Type détecté | Exemple |
|--------|--------------|---------|
| `1` | Integer | `id=1` |
| `1.5` | Double | `cours=1.5` |
| `USD` | String | `code=USD` |

## 🎯 Tests avec curl (Windows PowerShell)

```powershell
# Connexion niveau 3 (admin)
curl -X POST "http://localhost:8080/change/api/auth/login" -H "Content-Type: application/json" -d "{\"userId\":3,\"roleLevel\":3}"

# Test filtres
curl "http://localhost:8080/change/api/devises?cours_gt=1.0"
curl "http://localhost:8080/change/api/devises?code_like=%25USD%25"
curl "http://localhost:8080/change/api/devises?dateFin_null=true&orderBy=cours"

# Déconnexion
curl -X POST "http://localhost:8080/change/api/auth/logout"
```

## 📊 Avantages

✅ **Flexibilité** : Ajout de nouveaux filtres sans modifier le code
✅ **Cohérence** : Même syntaxe pour tous les endpoints
✅ **Performance** : Filtrage côté base de données
✅ **Simplicité** : Syntaxe intuitive et auto-documentée

## 🔒 Sécurité

- Tous les filtres sont soumis aux vérifications de permissions
- Conversion automatique camelCase → snake_case
- Protection contre l'injection SQL via PreparedStatement
