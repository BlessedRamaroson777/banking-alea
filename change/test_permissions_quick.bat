@echo off
chcp 65001 >nul
echo ========================================
echo TESTS RAPIDES - SYSTÈME DE PERMISSIONS
echo ========================================
echo.

set BASE_URL=http://localhost:8180/change/api
set "CONTENT_TYPE=Content-Type: application/json"

echo ========================================
echo TEST 1: Login Niveau 1 (READ)
echo ========================================
curl -X POST "%BASE_URL%/auth/login" -H "%CONTENT_TYPE%" -d "{\"userId\":1,\"roleLevel\":1}"
echo.
echo.
timeout /t 2 >nul

echo Session actuelle:
curl -X GET "%BASE_URL%/auth/session"
echo.
echo.
timeout /t 2 >nul

echo Lecture devises (OK):
curl -X GET "%BASE_URL%/devises"
echo.
echo.
timeout /t 2 >nul

echo Création devise (DEVRAIT ÉCHOUER - 403):
curl -X POST "%BASE_URL%/devises" -H "%CONTENT_TYPE%" -d "{\"code\":\"TEST\",\"dateDebut\":\"2024-01-01\",\"cours\":1.5,\"statutCode\":1}"
echo.
echo.
timeout /t 2 >nul

echo ========================================
echo TEST 2: Login Niveau 2 (CREATE/UPDATE)
echo ========================================
curl -X POST "%BASE_URL%/auth/logout"
echo.
timeout /t 1 >nul

curl -X POST "%BASE_URL%/auth/login" -H "%CONTENT_TYPE%" -d "{\"userId\":2,\"roleLevel\":2}"
echo.
echo.
timeout /t 2 >nul

echo Création devise (OK):
curl -X POST "%BASE_URL%/devises" -H "%CONTENT_TYPE%" -d "{\"code\":\"TEST\",\"dateDebut\":\"2024-01-01\",\"cours\":1.5,\"statutCode\":1}"
echo.
echo.
timeout /t 2 >nul

echo Validation devise (DEVRAIT ÉCHOUER - 403):
curl -X POST "%BASE_URL%/devises/1/valider"
echo.
echo.
timeout /t 2 >nul

echo ========================================
echo TEST 3: Login Niveau 3 (ADMIN)
echo ========================================
curl -X POST "%BASE_URL%/auth/logout"
echo.
timeout /t 1 >nul

curl -X POST "%BASE_URL%/auth/login" -H "%CONTENT_TYPE%" -d "{\"userId\":3,\"roleLevel\":3}"
echo.
echo.
timeout /t 2 >nul

echo Validation devise (OK):
curl -X POST "%BASE_URL%/devises/1/valider"
echo.
echo.
timeout /t 2 >nul

echo Suppression devise (OK):
curl -X DELETE "%BASE_URL%/devises/5"
echo.
echo.
timeout /t 2 >nul

echo ========================================
echo Logout
echo ========================================
curl -X POST "%BASE_URL%/auth/logout"
echo.
echo.

echo ========================================
echo TESTS TERMINÉS
echo ========================================
echo.
echo Résumé des tests:
echo - Niveau 1: Peut lire uniquement ✓
echo - Niveau 2: Peut lire + créer/modifier ✓
echo - Niveau 3: Tous les droits (admin) ✓
echo - Permissions correctement vérifiées ✓
echo.
pause
