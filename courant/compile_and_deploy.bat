@echo off
echo ========================================
echo COMPILATION ET DEPLOIEMENT - COURANT
echo ========================================
echo.

set PROJECT_DIR=%~dp0
set WILDFLY_DEPLOY_DIR=C:\Users\Tony\Desktop\Dossier personnel\Servers\wildfly-37.0.1.Final\standalone\deployments
set WAR_FILE=courant.war

echo [1/3] Compilation du projet...
cd "%PROJECT_DIR%"
echo Lancement de Maven...
call mvn clean package -DskipTests
echo Fin de Maven, vérification errorlevel...
if %errorlevel% neq 0 (
    echo Erreur lors de la compilation !
    pause
    exit /b 1
)
echo Compilation réussie.
echo.

echo [2/3] Nettoyage des anciens déploiements...
del /Q "%WILDFLY_DEPLOY_DIR%\courant-1.0-SNAPSHOT.war" >nul 2>&1
del /Q "%WILDFLY_DEPLOY_DIR%\courant-1.0-SNAPSHOT.war.deployed" >nul 2>&1
del /Q "%WILDFLY_DEPLOY_DIR%\courant-1.0-SNAPSHOT.war.failed" >nul 2>&1
del /Q "%WILDFLY_DEPLOY_DIR%\courant-1.0-SNAPSHOT.war.isdeploying" >nul 2>&1
del /Q "%WILDFLY_DEPLOY_DIR%\courant.war.deployed" >nul 2>&1
del /Q "%WILDFLY_DEPLOY_DIR%\courant.war.failed" >nul 2>&1
del /Q "%WILDFLY_DEPLOY_DIR%\courant.war.isdeploying" >nul 2>&1
if exist "%WILDFLY_DEPLOY_DIR%\courant.war" rmdir /S /Q "%WILDFLY_DEPLOY_DIR%\courant.war"
echo Copie du WAR vers WildFly...
copy "target\%WAR_FILE%" "%WILDFLY_DEPLOY_DIR%\"
if %errorlevel% neq 0 (
    echo Erreur lors de la copie !
    pause
    exit /b 1
)
echo WAR copié avec succès vers %WILDFLY_DEPLOY_DIR%.
echo.

echo [3/3] Déploiement terminé.
echo Le WAR %WAR_FILE% est maintenant dans le répertoire de déploiement de WildFly.
echo Vérifiez les logs de WildFly pour confirmer le déploiement.
echo.
pause