@echo off
setlocal
cd /d "%~dp0"

set "APP_DIR=%~dp0PhanMem_PhongKham_NhaKhoa"
set "DRIVER=%APP_DIR%\lib\mssql-jdbc-13.2.1.jre11.jar"
set "CLASSES=%APP_DIR%\bin"

rem Use JAVA_HOME when javac is installed but not added to PATH.
if defined JAVA_HOME if exist "%JAVA_HOME%bin\javac.exe" set "PATH=%JAVA_HOME%bin;%PATH%"
if not defined JAVA_HOME (
    for %%J in ("%ProgramFiles%\Eclipse Adoptium\jdk-*" "%ProgramFiles%\Java\jdk-*") do (
        if exist "%%~J\bin\javac.exe" set "PATH=%%~J\bin;%PATH%"
    )
)

where javac >nul 2>nul
if errorlevel 1 (
    echo JDK compiler javac.exe was not found.
    echo Install a JDK or set JAVA_HOME to its folder, for example:
    echo   set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17...
    pause
    exit /b 1
)

if not exist "%DRIVER%" (
    echo Missing SQL Server JDBC driver:
    echo   %DRIVER%
    pause
    exit /b 1
)

if not exist "%CLASSES%" mkdir "%CLASSES%"

dir /s /b "%APP_DIR%\src\*.java" > "%CLASSES%\sources.list"
javac -encoding UTF-8 -cp "%DRIVER%" -d "%CLASSES%" @"%CLASSES%\sources.list"
if errorlevel 1 (
    echo.
    echo Build failed.
    pause
    exit /b 1
)

java -Djava.library.path="%APP_DIR%\lib" -cp "%CLASSES%;%DRIVER%" gui.LoginModern
if errorlevel 1 pause
endlocal
