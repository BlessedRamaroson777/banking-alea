@echo off
chcp 65001 > nul
echo =========================================
echo    TEST DES FILTRES GENERALISÉS
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
echo    TESTS FILTRES NUMÉRIQUES
echo =========================================
echo.

echo [TEST 1] Devises avec cours ^> 1.0
echo URL: %BASE_URL%/devises?cours_gt=1.0
curl -s "%BASE_URL%/devises?cours_gt=1.0"
echo.
echo.

echo [TEST 2] Devises avec cours ^< 100.0
echo URL: %BASE_URL%/devises?cours_lt=100.0
curl -s "%BASE_URL%/devises?cours_lt=100.0"
echo.
echo.

echo [TEST 3] Devises avec cours ^>= 0.5
echo URL: %BASE_URL%/devises?cours_gte=0.5
curl -s "%BASE_URL%/devises?cours_gte=0.5"
echo.
echo.

echo [TEST 4] Devises avec cours ^<= 2.0
echo URL: %BASE_URL%/devises?cours_lte=2.0
curl -s "%BASE_URL%/devises?cours_lte=2.0"
echo.
echo.

echo [TEST 5] Devises avec cours entre 0.5 et 2.0
echo URL: %BASE_URL%/devises?cours_gte=0.5^&cours_lte=2.0
curl -s "%BASE_URL%/devises?cours_gte=0.5&cours_lte=2.0"
echo.
echo.

echo =========================================
echo    TESTS FILTRES LIKE
echo =========================================
echo.

echo [TEST 6] Devises contenant 'US'
echo URL: %BASE_URL%/devises?code_like=%%25US%%25
curl -s "%BASE_URL%/devises?code_like=%%25US%%25"
echo.
echo.

echo [TEST 7] Devises commençant par 'E'
echo URL: %BASE_URL%/devises?code_like=E%%25
curl -s "%BASE_URL%/devises?code_like=E%%25"
echo.
echo.

echo =========================================
echo    TESTS FILTRES NULL
echo =========================================
echo.

echo [TEST 8] Devises sans date de fin (IS NULL)
echo URL: %BASE_URL%/devises?dateFin_null=true
curl -s "%BASE_URL%/devises?dateFin_null=true"
echo.
echo.

echo [TEST 9] Devises avec date de fin (IS NOT NULL)
echo URL: %BASE_URL%/devises?dateFin_null=false
curl -s "%BASE_URL%/devises?dateFin_null=false"
echo.
echo.

echo =========================================
echo    TESTS FILTRES COMBINÉS
echo =========================================
echo.

echo [TEST 10] Filtres multiples + tri + limite
echo URL: %BASE_URL%/devises?cours_gte=0.5^&dateFin_null=true^&orderBy=code^&limit=5
curl -s "%BASE_URL%/devises?cours_gte=0.5&dateFin_null=true&orderBy=code&limit=5"
echo.
echo.

echo [TEST 11] Égalité + NOT EQUAL
echo URL: %BASE_URL%/devises?statutCode_ne=0^&orderBy=cours
curl -s "%BASE_URL%/devises?statutCode_ne=0&orderBy=cours"
echo.
echo.

echo =========================================
echo    TESTS DEVISES-MODIFICATIONS
echo =========================================
echo.

echo [TEST 12] Modifications avec deviseId et statut
echo URL: %BASE_URL%/devises-modifications?deviseId=1^&statutValidation=1
curl -s "%BASE_URL%/devises-modifications?deviseId=1&statutValidation=1"
echo.
echo.

echo [TEST 13] Modifications avec cours ^> 1.0
echo URL: %BASE_URL%/devises-modifications?cours_gt=1.0
curl -s "%BASE_URL%/devises-modifications?cours_gt=1.0"
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
pause
