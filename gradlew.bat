@rem Gradle startup script for Windows
@if "%DEBUG%"=="" @echo off
@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal
set APP_HOME=%~dp0
set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
set JAVA_EXE=java.exe
for /f "tokens=*" %%g in ('where %JAVA_EXE%') do (set JAVA_PATH=%%g)
%JAVA_PATH% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
