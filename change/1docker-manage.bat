@echo off
chcp 65001 >nul
setlocal EnableDelayedExpansion

:menu
cls
echo.
echo ========================================
echo   GESTION CONTENEUR DOCKER
echo ========================================
echo.
echo [1] Démarrer le conteneur
echo [2] Arrêter le conteneur
echo [3] Redémarrer le conteneur
echo [4] Voir les logs
echo [5] Voir le statut
echo [6] Supprimer le conteneur
echo [7] Rebuild complet
echo [0] Quitter
echo.
set /p choice="Votre choix : "

if "%choice%"=="1" goto start
if "%choice%"=="2" goto stop
if "%choice%"=="3" goto restart
if "%choice%"=="4" goto logs
if "%choice%"=="5" goto status
if "%choice%"=="6" goto remove
if "%choice%"=="7" goto rebuild
if "%choice%"=="0" exit /b 0
goto menu

:start
echo.
echo Démarrage du conteneur...
docker start change-app
echo.
pause
goto menu

:stop
echo.
echo Arrêt du conteneur...
docker stop change-app
echo.
pause
goto menu

:restart
echo.
echo Redémarrage du conteneur...
docker restart change-app
echo.
pause
goto menu

:logs
echo.
echo Logs du conteneur (Ctrl+C pour quitter) :
echo.
docker logs -f change-app
goto menu

:status
echo.
echo Statut du conteneur :
echo.
docker ps -a --filter name=change-app
echo.
echo.
echo Statistiques :
docker stats change-app --no-stream
echo.
pause
goto menu

:remove
echo.
echo Suppression du conteneur...
docker stop change-app 2>nul
docker rm change-app
echo.
pause
goto menu

:rebuild
echo.
echo Rebuild complet (build + run)...
call docker-build.bat
pause
goto menu
