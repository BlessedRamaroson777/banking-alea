@echo off
rem ------------------------------------------------------------------
rem deploy-all.bat
rem Runs the three deploy.bat scripts in this repository: courant, change, central
rem Usage: double-click or run from a shell in the repo root
rem ------------------------------------------------------------------

setlocal enabledelayedexpansion

rem ensure we run from the directory where this script lives
pushd "%~dp0"

set rc_courant=0
set rc_change=0
set rc_central=0

echo ==================================================
echo Running deploy for module: courant
echo ==================================================
if exist "courant\deploy.bat" (
	pushd "courant"
	call deploy.bat
	set rc_courant=%ERRORLEVEL%
	popd
) else (
	echo WARNING: courant\deploy.bat not found
	set rc_courant=1
)

echo.
echo ==================================================
echo Running deploy for module: change
echo ==================================================
if exist "change\deploy.bat" (
	pushd "change"
	call deploy.bat
	set rc_change=%ERRORLEVEL%
	popd
) else (
	echo WARNING: change\deploy.bat not found
	set rc_change=1
)

echo.
echo ==================================================
echo Running deploy for module: central
echo ==================================================
if exist "central\deploy.bat" (
	pushd "central"
	call deploy.bat
	set rc_central=%ERRORLEVEL%
	popd
) else (
	echo WARNING: central\deploy.bat not found
	set rc_central=1
)

popd

set /a failures=0
if %rc_courant% neq 0 set /a failures+=1
if %rc_change% neq 0 set /a failures+=1
if %rc_central% neq 0 set /a failures+=1

echo.
if %failures% neq 0 (
	echo ONE OR MORE DEPLOYS FAILED (courant=%rc_courant% change=%rc_change% central=%rc_central%)
	exit /b 1
) else (
	echo ALL DEPLOYS COMPLETED SUCCESSFULLY
	exit /b 0
)
