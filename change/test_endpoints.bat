@echo off
chcp 65001 >nul
echo ========================================
echo TEST DES ENDPOINTS REST - BANQUE CHANGE
echo ========================================
echo.

set BASE_URL=http://localhost:8180/change/api
set "CONTENT_TYPE=Content-Type: application/json"

echo ========================================
echo 1. TEST AUTHENTIFICATION
echo ========================================
echo.

echo [TEST 1.1] Login avec utilisateur niveau 1 (READ only)
curl -X POST "%BASE_URL%/auth/login" -H "%CONTENT_TYPE%" -d "{\"userId\":1,\"roleLevel\":1}"
echo.
echo.

echo [TEST 1.2] Vérifier la session
curl -X GET "%BASE_URL%/auth/session"
echo.
echo.

echo [TEST 1.3] Vérifier permission READ sur devises
curl -X GET "%BASE_URL%/auth/permissions/devises/READ"
echo.
echo.

echo [TEST 1.4] Vérifier permission CREATE sur devises (devrait échouer)
curl -X GET "%BASE_URL%/auth/permissions/devises/CREATE"
echo.
echo.

echo ========================================
echo 2. TEST DEVISES (avec niveau 1 - READ only)
echo ========================================
echo.

echo [TEST 2.1] Lire toutes les devises (devrait réussir)
curl -X GET "%BASE_URL%/devises"
echo.
echo.

echo [TEST 2.2] Lire une devise par ID (devrait réussir)
curl -X GET "%BASE_URL%/devises/1"
echo.
echo.

echo [TEST 2.3] Créer une devise (devrait échouer - permission refusée)
curl -X POST "%BASE_URL%/devises" -H "%CONTENT_TYPE%" -d "{\"code\":\"GBP\",\"dateDebut\":\"2024-01-01\",\"cours\":0.85,\"statutCode\":1}"
echo.
echo.

echo ========================================
echo 3. LOGOUT ET RE-LOGIN (niveau 2)
echo ========================================
echo.

echo [TEST 3.1] Logout
curl -X POST "%BASE_URL%/auth/logout"
echo.
echo.

echo [TEST 3.2] Login avec utilisateur niveau 2 (READ + CREATE/UPDATE)
curl -X POST "%BASE_URL%/auth/login" -H "%CONTENT_TYPE%" -d "{\"userId\":2,\"roleLevel\":2}"
echo.
echo.

echo [TEST 3.3] Vérifier la session
curl -X GET "%BASE_URL%/auth/session"
echo.
echo.

echo ========================================
echo 4. TEST DEVISES (avec niveau 2)
echo ========================================
echo.

echo [TEST 4.1] Créer une devise (devrait réussir)
curl -X POST "%BASE_URL%/devises" -H "%CONTENT_TYPE%" -d "{\"code\":\"GBP\",\"dateDebut\":\"2024-01-01\",\"cours\":0.85,\"statutCode\":1}"
echo.
echo.

echo [TEST 4.2] Modifier une devise (devrait réussir)
curl -X PUT "%BASE_URL%/devises/1" -H "%CONTENT_TYPE%" -d "{\"id\":1,\"code\":\"USD\",\"dateDebut\":\"2023-01-01\",\"cours\":1.05,\"statutCode\":2}"
echo.
echo.

echo [TEST 4.3] Valider une devise (devrait échouer - nécessite niveau 3)
curl -X POST "%BASE_URL%/devises/1/valider" -H "%CONTENT_TYPE%" -d "{\"dateValidation\":\"2025-11-05\"}"
echo.
echo.

echo [TEST 4.4] Supprimer une devise (devrait échouer - nécessite niveau 3)
curl -X DELETE "%BASE_URL%/devises/1"
echo.
echo.

echo ========================================
echo 5. TEST MODIFICATIONS DE DEVISES
echo ========================================
echo.

echo [TEST 5.1] Proposer une modification (devrait réussir avec niveau 2)
curl -X POST "%BASE_URL%/devises/1/propose-modification" -H "%CONTENT_TYPE%" -d "{\"code\":\"USD\",\"cours\":1.10,\"dateDebut\":\"2023-01-01\",\"statutCode\":2}"
echo.
echo.

echo [TEST 5.2] Lire les modifications en attente
curl -X GET "%BASE_URL%/devises-modifications/en-attente"
echo.
echo.

echo [TEST 5.3] Valider une modification (devrait échouer - nécessite niveau 3)
curl -X POST "%BASE_URL%/devises-modifications/1/valider" -H "%CONTENT_TYPE%" -d "{\"dateValidation\":\"2025-11-05\"}"
echo.
echo.

echo ========================================
echo 6. LOGOUT ET RE-LOGIN (niveau 3 - ADMIN)
echo ========================================
echo.

echo [TEST 6.1] Logout
curl -X POST "%BASE_URL%/auth/logout"
echo.
echo.

echo [TEST 6.2] Login avec utilisateur niveau 3 (ADMIN - tous droits)
curl -X POST "%BASE_URL%/auth/login" -H "%CONTENT_TYPE%" -d "{\"userId\":3,\"roleLevel\":3}"
echo.
echo.

echo [TEST 6.3] Vérifier la session
curl -X GET "%BASE_URL%/auth/session"
echo.
echo.

echo ========================================
echo 7. TEST OPÉRATIONS ADMIN (niveau 3)
echo ========================================
echo.

echo [TEST 7.1] Lire les devises en attente
curl -X GET "%BASE_URL%/devises/en-attente"
echo.
echo.

echo [TEST 7.2] Valider une devise créée (devrait réussir)
curl -X POST "%BASE_URL%/devises/4/valider" -H "%CONTENT_TYPE%" -d "{\"dateValidation\":\"2025-11-05\"}"
echo.
echo.

echo [TEST 7.3] Lire les modifications en attente
curl -X GET "%BASE_URL%/devises-modifications/en-attente"
echo.
echo.

echo [TEST 7.4] Valider une modification (devrait réussir)
curl -X POST "%BASE_URL%/devises-modifications/1/valider" -H "%CONTENT_TYPE%" -d "{\"dateValidation\":\"2025-11-05\"}"
echo.
echo.

echo [TEST 7.5] Refuser une modification
curl -X POST "%BASE_URL%/devises-modifications/2/refuser"
echo.
echo.

echo [TEST 7.6] Supprimer une devise (devrait réussir)
curl -X DELETE "%BASE_URL%/devises/4"
echo.
echo.

echo [TEST 7.7] Supprimer une modification
curl -X DELETE "%BASE_URL%/devises-modifications/3"
echo.
echo.

echo ========================================
echo 8. TEST RECHARGEMENT DES PERMISSIONS
echo ========================================
echo.

echo [TEST 8.1] Recharger les permissions
curl -X POST "%BASE_URL%/auth/refresh-permissions"
echo.
echo.

echo ========================================
echo 9. TEST FILTRES AVANCÉS
echo ========================================
echo.

echo [TEST 9.1] Filtrer devises par code
curl -X GET "%BASE_URL%/devises?code=USD"
echo.
echo.

echo [TEST 9.2] Filtrer devises avec cours supérieur à 1.0
curl -X GET "%BASE_URL%/devises?cours_gt=1.0"
echo.
echo.

echo [TEST 9.3] Filtrer devises avec date_fin IS NULL
curl -X GET "%BASE_URL%/devises?date_fin_null=true"
echo.
echo.

echo ========================================
echo 10. LOGOUT FINAL
echo ========================================
echo.

echo [TEST 10.1] Logout final
curl -X POST "%BASE_URL%/auth/logout"
echo.
echo.

echo [TEST 10.2] Vérifier la session (devrait être vide)
curl -X GET "%BASE_URL%/auth/session"
echo.
echo.

echo [TEST 10.3] Tenter une opération sans authentification (devrait échouer)
curl -X GET "%BASE_URL%/devises"
echo.
echo.

echo ========================================
echo TESTS TERMINÉS
echo ========================================
pause
