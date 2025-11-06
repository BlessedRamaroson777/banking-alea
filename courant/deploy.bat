@echo off
REM filepath: d:\ITU\S5\Architecture logiciel (Mr Tahina)\Banque\centralisateur\deploy.bat

echo Deploying courant.war to WildFly...
call mvn clean package
REM Check if target directory exists
if not exist "target\" (
    echo Error: target directory not found. Please build the project first.
    pause
    exit /b 1
)

REM Check if WAR file exists
if not exist "target\courant.war" (
    echo Error: courant.war not found in target directory. Please build the project first.
    pause
    exit /b 1
)

REM Check if WildFly deployments directory exists
if not exist "C:\wildfly\standalone\deployments" (
    echo Error: WildFly deployments directory not found.
    pause
    exit /b 1
)

REM Copy WAR file to WildFly deployments
copy "target\courant.war" "C:\wildfly\standalone\deployments"

if %errorlevel% equ 0 (
    echo Successfully deployed courant.war to WildFly!
) else (
    echo Error occurred during deployment.
)

