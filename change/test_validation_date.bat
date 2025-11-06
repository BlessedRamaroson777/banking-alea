@echo off
chcp 65001 > nul
echo =========================================
echo    TEST VALIDATION AVEC DATE
echo =========================================
echo.

REM Configuration
set BASE_URL=http://localhost:8180/change/api

echo [ÉTAPE 1] Connexion niveau 3 (admin)
curl -s -X POST "%BASE_URL%/auth/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"userId\":3,\"roleLevel\":3}"
echo.
echo.

timeout /t 2 /nobreak > nul

echo =========================================
echo    CRÉATION D'UNE DEVISE DE TEST
echo =========================================
echo.

echo [TEST 1] Créer une nouvelle devise en attente
curl -s -X POST "%BASE_URL%/devises" ^
  -H "Content-Type: application/json" ^
  -d "{\"code\":\"TEST\",\"cours\":1.5,\"dateDebut\":\"2025-11-05\",\"statutCode\":1}"
echo.
echo.

timeout /t 1 /nobreak > nul

echo =========================================
echo    VALIDATION SANS DATE (date actuelle)
echo =========================================
echo.

echo [TEST 2] Valider la devise sans spécifier de date
echo URL: %BASE_URL%/devises/4/valider
echo Body: {}
curl -s -X POST "%BASE_URL%/devises/4/valider" ^
  -H "Content-Type: application/json" ^
  -d "{}"
echo.
echo.

timeout /t 1 /nobreak > nul

echo [TEST 3] Vérifier que la devise a été validée
echo URL: %BASE_URL%/devises/4
curl -s "%BASE_URL%/devises/4"
echo.
echo.

echo =========================================
echo    CRÉATION NOUVELLE DEVISE POUR DATE
echo =========================================
echo.

echo [TEST 4] Créer une autre devise en attente
curl -s -X POST "%BASE_URL%/devises" ^
  -H "Content-Type: application/json" ^
  -d "{\"code\":\"TEST2\",\"cours\":2.5,\"dateDebut\":\"2025-11-05\",\"statutCode\":1}"
echo.
echo.

timeout /t 1 /nobreak > nul

echo =========================================
echo    VALIDATION AVEC DATE SPÉCIFIQUE
echo =========================================
echo.

echo [TEST 5] Valider la devise avec date spécifique (2025-12-01)
echo URL: %BASE_URL%/devises/5/valider
echo Body: {"dateValidation":"2025-12-01"}
curl -s -X POST "%BASE_URL%/devises/5/valider" ^
  -H "Content-Type: application/json" ^
  -d "{\"dateValidation\":\"2025-12-01\"}"
echo.
echo.

timeout /t 1 /nobreak > nul

echo [TEST 6] Vérifier que la date de début a été mise à jour
echo URL: %BASE_URL%/devises/5
curl -s "%BASE_URL%/devises/5"
echo.
echo.

echo =========================================
echo    TESTS D'ERREUR
echo =========================================
echo.

echo [TEST 7] Tenter de valider une devise déjà validée (doit échouer)
echo URL: %BASE_URL%/devises/1/valider
curl -s -X POST "%BASE_URL%/devises/1/valider" ^
  -H "Content-Type: application/json" ^
  -d "{\"dateValidation\":\"2025-11-05\"}"
echo.
echo.

echo =========================================
echo    NETTOYAGE
echo =========================================
echo.

echo [NETTOYAGE] Suppression des devises de test
curl -s -X DELETE "%BASE_URL%/devises/4"
echo.
curl -s -X DELETE "%BASE_URL%/devises/5"
echo.
echo.

echo =========================================
echo    DÉCONNEXION
echo =========================================
echo.

curl -s -X POST "%BASE_URL%/auth/logout"
echo.
echo.

echo =========================================
echo    TESTS TERMINÉS
echo =========================================
echo.
echo RÉSUMÉ:
echo - TEST 2: Validation sans date (utilise date actuelle)
echo - TEST 5: Validation avec date spécifique (2025-12-01)
echo - TEST 7: Validation d'une devise déjà validée (doit échouer)
echo.
pause
