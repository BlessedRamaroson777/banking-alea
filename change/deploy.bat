@echo off
REM ===========================================
REM Script de déploiement sur WildFly
REM ===========================================

echo.
echo ========================================
echo   DEPLOIEMENT SUR WILDFLY
echo ========================================
echo.

REM Chemin vers WildFly
set WILDFLY_HOME=C:\wildfly
set DEPLOYMENT_DIR=%WILDFLY_HOME%\standalone\deployments

REM Vérifier si WildFly existe
if not exist "%WILDFLY_HOME%" (
    echo [ERREUR] WildFly non trouve a : %WILDFLY_HOME%
    echo.
    pause
    exit /b 1
)

REM Nettoyer le projet
echo [1/5] Nettoyage du projet...
call mvn clean
if %ERRORLEVEL% neq 0 (
    echo [ERREUR] Echec du nettoyage
    pause
    exit /b 1
)
echo      [OK] Nettoyage termine
echo.

REM Compiler et packager
echo [2/5] Compilation et packaging...
call mvn package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo [ERREUR] Echec de la compilation
    pause
    exit /b 1
)
echo      [OK] WAR genere avec succes
echo.

REM Vérifier que le WAR existe
if not exist "target\change.war" (
    echo [ERREUR] Le fichier WAR n'a pas ete genere
    pause
    exit /b 1
)

REM Supprimer l'ancien déploiement
echo [3/5] Suppression de l'ancien deploiement...
if exist "%DEPLOYMENT_DIR%\change.war" (
    del /F /Q "%DEPLOYMENT_DIR%\change.war"
    echo      [OK] Ancien WAR supprime
)
if exist "%DEPLOYMENT_DIR%\change.war.deployed" (
    del /F /Q "%DEPLOYMENT_DIR%\change.war.deployed"
)
if exist "%DEPLOYMENT_DIR%\change.war.failed" (
    del /F /Q "%DEPLOYMENT_DIR%\change.war.failed"
)
if exist "%DEPLOYMENT_DIR%\change.war.isdeploying" (
    del /F /Q "%DEPLOYMENT_DIR%\change.war.isdeploying"
)
echo.

REM Copier le nouveau WAR
echo [4/5] Copie du nouveau WAR...
copy /Y "target\change.war" "%DEPLOYMENT_DIR%\"
if %ERRORLEVEL% neq 0 (
    echo [ERREUR] Echec de la copie du WAR
    pause
    exit /b 1
)
echo      [OK] WAR copie vers WildFly
echo.

REM Attendre le déploiement
echo [5/5] Attente du deploiement...
timeout /t 5 /nobreak >nul

REM Vérifier le déploiement
if exist "%DEPLOYMENT_DIR%\change.war.deployed" (
    echo.
    echo ========================================
    echo   DEPLOIEMENT REUSSI !
    echo ========================================
    echo.
    echo Application deployee : change.war
    echo.
) else if exist "%DEPLOYMENT_DIR%\change.war.failed" (
    echo.
    echo ========================================
    echo   DEPLOIEMENT ECHOUE !
    echo ========================================
    echo.
    echo Verifiez les logs WildFly pour plus de details
    echo Logs : %WILDFLY_HOME%\standalone\log\server.log
    echo.
) else (
    echo.
    echo [ATTENTION] Le deploiement est en cours...
    echo Verifiez dans quelques secondes le dossier :
    echo %DEPLOYMENT_DIR%
    echo.
)
