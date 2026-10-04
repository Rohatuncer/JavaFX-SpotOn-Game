@echo off
setlocal

pushd "%~dp0"

set "TOOLKIT=%USERPROFILE%\Desktop\JavaFX-Toolkit\JavaFX-Toolkit"
set "JAVA_HOME=%TOOLKIT%\bellsoft-jdk25.0.1+11-windows-amd64-full\jdk-25.0.1-full"
set "JAVAC=%JAVA_HOME%\bin\javac.exe"

if not exist "%JAVAC%" (
    echo Java compiler not found:
    echo %JAVAC%
    echo.
    echo Check that JavaFX-Toolkit is on your Desktop.
    pause
    exit /b 1
)

if not exist out mkdir out

echo Compiling SpotOn...
"%JAVAC%" -d out -sourcepath src src\spoton\*.java

if errorlevel 1 (
    echo.
    echo Compilation failed.
    pause
    exit /b 1
)

echo.
echo Compilation successful.
echo Run run_spoton.bat to start the game.
pause

popd
endlocal
