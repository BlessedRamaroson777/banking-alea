@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

echo.
echo ========================================
echo   LANCEMENT DU CONTENEUR DOCKER
echo ========================================
echo.

REM Vérifier que l'image locale "wildfly" existe
docker image inspect wildfly:latest >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERREUR] L'image locale "wildfly:latest" n'existe pas !
    echo Veuillez d'abord la créer ou la télécharger.
    exit /b 1
)

REM Copier le fichier de configuration Docker
echo [1/5] Préparation de la configuration Docker...
copy /Y "src\main\resources\db.properties.docker" "src\main\resources\db.properties" >nul
if %errorlevel% equ 0 (
    echo      [OK] Configuration Docker activée
) else (
    echo      [ERREUR] Échec de la copie de configuration
    exit /b 1
)

REM Compiler le projet avec Maven
echo.
echo [2/5] Compilation du projet avec Maven...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo      [ERREUR] Échec de la compilation Maven
    exit /b 1
)
echo      [OK] Projet compilé

REM Arrêter et supprimer le conteneur existant si présent
echo.
echo [3/5] Nettoyage des conteneurs existants...
docker stop change-app 2>nul
docker rm change-app 2>nul
echo      [OK] Nettoyage terminé

REM Lancer le conteneur directement depuis l'image locale
echo.
echo [4/5] Lancement du conteneur depuis l'image "wildfly:latest"...
docker run -d ^
    --name change-app ^
    --add-host=host.docker.internal:host-gateway ^
    -p 8180:8180 ^
    -p 9991:9991 ^
    wildfly:latest

if %errorlevel% neq 0 (
    echo      [ERREUR] Échec du lancement du conteneur
    exit /b 1
)
echo      [OK] Conteneur lancé

REM Restaurer le fichier de configuration local
echo.
echo [5/5] Restauration de la configuration locale...
git checkout src\main\resources\db.properties 2>nul
echo      [OK] Configuration locale restaurée

echo.
echo ========================================
echo   CONTENEUR DOCKER LANCÉ !
echo ========================================
echo.
echo Application Docker disponible sur :
echo   - HTTP : http://localhost:8180/change/api
echo   - Management : http://localhost:9991
echo.
echo Logs en temps réel :
echo   docker logs -f change-app
echo.
echo Arrêter le conteneur :
echo   docker stop change-app
echo.
echo Redémarrer le conteneur :
echo   docker start change-app
echo.
pause
