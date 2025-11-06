@echo off
REM Script Batch pour mettre à jour le WAR dans le conteneur Docker sans recréer l'image
REM Remplacez 'change-container' par le nom ou l'ID de votre conteneur Docker

set CONTAINER_NAME=change-app
call mvn clean package
REM Vérifier si le fichier WAR existe
if not exist "target\change.war" (
    echo Erreur : target\change.war n'existe pas. Veuillez d'abord construire le projet avec 'mvn clean package'.
    pause
    exit /b 1
)

REM Copier le nouveau WAR dans le conteneur
echo Copie du WAR vers le conteneur...
docker cp "target\change.war" "%CONTAINER_NAME%:/opt/jboss/wildfly/standalone/deployments/change.war"

if errorlevel 1 (
    echo Erreur lors de la copie du WAR.
    pause
    exit /b 1
)

REM Forcer le redéploiement en touchant le fichier .dodeploy
echo Forçage du redéploiement...
docker exec %CONTAINER_NAME% touch "/opt/jboss/wildfly/standalone/deployments/change.war.dodeploy"

if errorlevel 1 (
    echo Erreur lors du forçage du redéploiement.
    pause
    exit /b 1
)

echo Mise à jour terminée. Le service devrait se redéployer automatiquement.
pause

@REM docker run -d --name change-container -p 8180:8180 -p 9991:9991 change