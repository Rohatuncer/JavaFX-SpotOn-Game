@echo off
setlocal

pushd "%~dp0"

set "TOOLKIT=%USERPROFILE%\Desktop\JavaFX-Toolkit\JavaFX-Toolkit"
set "JAVA_HOME=%TOOLKIT%\bellsoft-jdk25.0.1+11-windows-amd64-full\jdk-25.0.1-full"
set "JAVA=%JAVA_HOME%\bin\java.exe"

if not exist "%JAVA%" (
    echo Java runtime not found:
    echo %JAVA%
    echo.
    echo Check that JavaFX-Toolkit is on your Desktop.
    pause
    exit /b 1
)

if not exist out\spoton\Main.class (
    echo Compiled files were not found.
    echo Run compile_spoton.bat first.
    pause
    exit /b 1
)

echo Starting SpotOn...
"%JAVA%" -cp out spoton.Main

if errorlevel 1 (
    echo.
    echo Game failed to start.
    pause
    exit /b 1
)

popd
endlocal
