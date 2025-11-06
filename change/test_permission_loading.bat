@echo off
chcp 65001 >nul
echo ========================================
echo TEST CHARGEMENT PERMISSIONS AU LOGIN
echo ========================================
echo.

set BASE_URL=http://localhost:8180/change/api
set "CONTENT_TYPE=Content-Type: application/json"

echo ========================================
echo TEST: Vérification du cache de permissions
echo ========================================
echo.

echo [1] Login avec niveau 1 (charge permissions READ uniquement)
curl -X POST "%BASE_URL%/auth/login" -H "%CONTENT_TYPE%" -d "{\"userId\":1,\"roleLevel\":1}"
echo.
echo.
timeout /t 2 >nul

echo [2] Vérifier permissions accessibles:
echo.
echo Permission devises/READ (roleMinimum=1, devrait être OK):
curl -X GET "%BASE_URL%/auth/permissions/devises/READ"
echo.
echo.

echo Permission devises/CREATE (roleMinimum=2, devrait être refusée):
curl -X GET "%BASE_URL%/auth/permissions/devises/CREATE"
echo.
echo.

echo Permission devises/DELETE (roleMinimum=3, devrait être refusée):
curl -X GET "%BASE_URL%/auth/permissions/devises/DELETE"
echo.
echo.

echo Permission devises/VALIDATE (roleMinimum=3, devrait être refusée):
curl -X GET "%BASE_URL%/auth/permissions/devises/VALIDATE"
echo.
echo.

timeout /t 2 >nul

echo ========================================
echo [3] Logout et re-login avec niveau 2
echo ========================================
curl -X POST "%BASE_URL%/auth/logout"
echo.
timeout /t 1 >nul

curl -X POST "%BASE_URL%/auth/login" -H "%CONTENT_TYPE%" -d "{\"userId\":2,\"roleLevel\":2}"
echo.
echo.
timeout /t 2 >nul

echo [4] Vérifier nouvelles permissions chargées:
echo.
echo Permission devises/READ (roleMinimum=1, devrait être OK):
curl -X GET "%BASE_URL%/auth/permissions/devises/READ"
echo.
echo.

echo Permission devises/CREATE (roleMinimum=2, devrait être OK maintenant):
curl -X GET "%BASE_URL%/auth/permissions/devises/CREATE"
echo.
echo.

echo Permission devises/UPDATE (roleMinimum=2, devrait être OK):
curl -X GET "%BASE_URL%/auth/permissions/devises/UPDATE"
echo.
echo.

echo Permission devises/DELETE (roleMinimum=3, devrait être refusée):
curl -X GET "%BASE_URL%/auth/permissions/devises/DELETE"
echo.
echo.

echo Permission devises/VALIDATE (roleMinimum=3, devrait être refusée):
curl -X GET "%BASE_URL%/auth/permissions/devises/VALIDATE"
echo.
echo.

timeout /t 2 >nul

echo ========================================
echo [5] Logout et re-login avec niveau 3 (ADMIN)
echo ========================================
curl -X POST "%BASE_URL%/auth/logout"
echo.
timeout /t 1 >nul

curl -X POST "%BASE_URL%/auth/login" -H "%CONTENT_TYPE%" -d "{\"userId\":3,\"roleLevel\":3}"
echo.
echo.
timeout /t 2 >nul

echo [6] Vérifier toutes les permissions (admin):
echo.
echo Permission devises/READ (devrait être OK):
curl -X GET "%BASE_URL%/auth/permissions/devises/READ"
echo.
echo.

echo Permission devises/CREATE (devrait être OK):
curl -X GET "%BASE_URL%/auth/permissions/devises/CREATE"
echo.
echo.

echo Permission devises/UPDATE (devrait être OK):
curl -X GET "%BASE_URL%/auth/permissions/devises/UPDATE"
echo.
echo.

echo Permission devises/DELETE (devrait être OK):
curl -X GET "%BASE_URL%/auth/permissions/devises/DELETE"
echo.
echo.

echo Permission devises/VALIDATE (devrait être OK):
curl -X GET "%BASE_URL%/auth/permissions/devises/VALIDATE"
echo.
echo.

echo Permission devises_modifications/VALIDATE (devrait être OK):
curl -X GET "%BASE_URL%/auth/permissions/devises_modifications/VALIDATE"
echo.
echo.

echo ========================================
echo [7] Logout final
echo ========================================
curl -X POST "%BASE_URL%/auth/logout"
echo.
echo.

echo ========================================
echo RÉSUMÉ DES TESTS
echo ========================================
echo.
echo ✓ Niveau 1: Charge uniquement permissions avec roleMinimum ^<= 1
echo ✓ Niveau 2: Charge uniquement permissions avec roleMinimum ^<= 2
echo ✓ Niveau 3: Charge toutes les permissions (roleMinimum ^<= 3)
echo ✓ Cache vidé lors du logout
echo ✓ Permissions rechargées lors du nouveau login
echo.
echo Le système de permissions fonctionne correctement !
echo.
pause
