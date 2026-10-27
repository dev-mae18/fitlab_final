@echo off
setlocal
cd /d "%~dp0"
if not defined JAVA_HOME if exist "C:\Program Files\Java\jdk-21.0.11\bin\java.exe" set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.11"
set "JAVA_EXE=java"
if defined JAVA_HOME set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
"%JAVA_EXE%" -Dmaven.multiModuleProjectDirectory="%CD%" -classpath "%~dp0.mvn\wrapper\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain javafx:run
if errorlevel 1 pause
endlocal
