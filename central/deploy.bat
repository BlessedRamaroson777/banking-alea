@echo off
REM filepath: d:\ITU\S5\Architecture logiciel (Mr Tahina)\Banque\centralisateur\deploy.bat

echo Deploying central.war to WildFly...
call mvn clean package
REM Check if target directory exists
if not exist "target\" (
    echo Error: target directory not found. Please build the project first.
    pause
    exit /b 1
)

REM Check if WAR file exists
if not exist "target\central.war" (
    echo Error: central.war not found in target directory. Please build the project first.
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
copy "target\central.war" "C:\wildfly\standalone\deployments"

if %errorlevel% equ 0 (
    echo Successfully deployed central.war to WildFly!
) else (
    echo Error occurred during deployment.
)
