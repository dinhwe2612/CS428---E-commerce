@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=

@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current directory if not found.

SET "PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR%"
IF NOT "%MAVEN_PROJECTBASEDIR%"=="" GOTO endDetectBaseDir

SET "EXEC_DIR=%CD%"
SET "WDIR=%EXEC_DIR%"

@REM Look for the --file switch and start the search for the .mvn folder from the specified
@REM POM location, if supplied.

SET "FILE_ARG=--file"
SET "POM_ARG="
FOR %%i IN ("%*") DO (
    IF "%FILE_ARG%"=="%%~i" (
        SET "POM_ARG=%%~i+1"
    ) ELSE IF "%POM_ARG%"=="%%~i" (
        SET "EXEC_DIR=%%~dpvi"
        SET "WDIR=%%~dpvi"
        GOTO findBaseDir
    )
)
GOTO findBaseDir

:findBaseDir
IF EXIST "%WDIR%\.mvn" GOTO baseDirFound
cd ..
IF "%WDIR%"=="%CD%" GOTO baseDirNotFound
SET "WDIR=%CD%"
GOTO findBaseDir

:baseDirFound
SET "MAVEN_PROJECTBASEDIR=%WDIR%"
cd "%EXEC_DIR%"
GOTO endDetectBaseDir

:baseDirNotFound
IF "_%EXEC_DIR:~-1%"=="_\" SET "EXEC_DIR=%EXEC_DIR:~0,-1%"
SET "MAVEN_PROJECTBASEDIR=%EXEC_DIR%"
cd "%EXEC_DIR%"

:endDetectBaseDir

SET "javaClass=org.apache.maven.wrapper.MavenWrapperMain"
SET "javaXmx64=64m"

IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config" GOTO endReadJvmConfig

@setlocal EnableExtensions EnableDelayedExpansion
FOR /F "usebackq delims=" %%a IN ("%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config") DO SET "jvmConfig=!jvmConfig! %%a"
@endlocal & SET "MAVEN_OPTS=%jvmConfig%"

:endReadJvmConfig

IF DEFINED MVNW_VERBOSE (
    ECHO MVNW_REPOURL=%MVNW_REPOURL%
    ECHO MVNW_USERNAME=%MVNW_USERNAME%
    ECHO MVNW_PASSWORD=%MVNW_PASSWORD%
    IF DEFINED MVNW_PASSWORD SET "MVNW_PASSWORD=********"
    ECHO.
)

SET "DOWNLOAD_URL_BASE=%MVNW_REPOURL%/org/apache/maven/wrapper/maven-wrapper/3.3.2"

FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties") DO (
    IF "%%A"=="wrapperVersion" SET "WRAPPER_VERSION=%%B"
    IF "%%A"=="distributionUrl" SET "WRAPPER_DISTRIBUTION_URL=%%B"
    IF "%%A"=="distributionType" SET "WRAPPER_DISTRIBUTION_TYPE=%%B"
    IF "%%A"=="wrapperJarPath" SET "WRAPPER_JAR_PATH=%%B"
)
IF %WRAPPER_VERSION% NEQ 3.3.2 (
    ECHO The Maven wrapper version is incompatible with this Maven Wrapper executable.
    EXIT /B 1
)

IF "%WRAPPER_DISTRIBUTION_TYPE%" == "only-script" (
    SET DOWNLOAD_URL=%DOWNLOAD_URL_BASE%/maven-wrapper-%WRAPPER_VERSION%.jar
    SET WRAPPER_JAR_POSTFIX=
) ELSE (
    SET DOWNLOAD_URL=%WRAPPER_DISTRIBUTION_URL%
    SET WRAPPER_JAR_POSTFIX=-bin
)

SET WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

IF DEFINED MVNW_USERNAME (
    SET USER_OPTION=-Dusername="%MVNW_USERNAME%"
)
IF DEFINED MVNW_PASSWORD (
    SET PASSWORD_OPTION=-Dpassword="%MVNW_PASSWORD%"
)

@REM Extension to allow automatically downloading the maven-wrapper.jar
SET WRAPPER_DOWNLOAD_SUCCESS=false
FOR %%B IN (%WRAPPER_JAR%) DO IF "%%~zB" GTR 0 SET WRAPPER_DOWNLOAD_SUCCESS=true
IF NOT %WRAPPER_DOWNLOAD_SUCCESS% (
    IF NOT EXIST "%WRAPPER_JAR%" (
        ECHO Couldn't find %WRAPPER_JAR%, downloading it ...
        ECHO Downloading from: %DOWNLOAD_URL%
    
        powershell -Command "Invoke-WebRequest -Uri '%DOWNLOAD_URL%' -OutFile '%WRAPPER_JAR%' -PassThru -UserAgent ''"
        
        set POWERSHELL_EXIT_CODE=%ERRORLEVEL%
        IF %POWERSHELL_EXIT_CODE% NEQ 0 (
            ECHO Unable to download with PowerShell.
            GOTO fallbackDownload
        )
        ECHO Maven wrapper jar downloaded from %DOWNLOAD_URL% successfully.
        GOTO startJ
    )
    :fallbackDownload
    IF NOT "_%MVNW_REPOURL%"=="_" (
        SET "wrapperUrlParam=-Dmaven.wrapperUrlPath=%MVNW_REPOURL%"
    )
    
    SET "sourceFile=%WRAPPER_JAR%"
    SET "destFile=%WRAPPER_JAR%"
    
    IF EXIST %WRAPPER_JAR% DEL /F %WRAPPER_JAR% > NUL
    
    SET JAVA_OPTS=%JAVA_OPTS% -Xmx%javaXmx64%
    SET JAVACMD=java
    SET WRAPPERJARFILE=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
    SET WRAPPERJARUTILMAIN=org.apache.maven.wrapper.MavenWrapperJarUtil
    
    set DOWNLOAD_URL=%DOWNLOAD_URL_BASE%/maven-wrapper-%WRAPPER_VERSION%.jar
    
    FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties") DO (
        IF "%%A"=="wrapperUrl" SET "WRAPPER_URL=%%B"
    )
    
    "%JAVACMD%" %JAVA_OPTS% %wrapperUrlParam% ^
        -jar %WRAPPERJARFILE% ^
        %WRAPPER_JARUTILMAIN% ^
        -djf %destFile% ^
        -durl "%DOWNLOAD_URL%" ^
        -v
    
    SET WRAPPER_DOWNLOAD_SUCCESS=false
    FOR %%B IN (%WRAPPER_JAR%) DO IF "%%~zB" GTR 0 SET WRAPPER_DOWNLOAD_SUCCESS=true
    
    IF NOT %WRAPPER_DOWNLOAD_SUCCESS% (
        ECHO Unable to download maven-wrapper.jar with both PowerShell and Java ...
        ECHO Ensure you have Java installed and working or Maven explicitly downloaded.
        GOTO error
    )
)

:startJ
@REM Start JAVA with the Maven Wrapper
SET MAVEN_CMD_LINE_ARGS=%*

@REM Split Java command and parameters
SET JAVACMD=java
FOR %%A IN (%JAVA_HOME%\bin\java.exe) DO SET "JAVACMD=%%~f$PATH:A"
IF "%JAVACMD%" NEQ "java" (
    IF EXIST "%JAVACMD%" GOTO javaexe
)
ECHO Java executable not found in PATH or JAVA_HOME...
GOTO error

:javaexe
IF NOT EXIST "%JAVACMD%" (
    ECHO Java executable not found at %JAVACMD%
    GOTO error
)

CALL SET RETVAL=0

"%JAVACMD%" %JAVA_OPTS% ^
    -jar %WRAPPER_JAR% ^
    %LAUNCHER_JAR% ^
    %MAVEN_CONFIG% %MAVEN_CMD_LINE_ARGS%

:endNT
IF %RETVAL% NEQ 0 (
    EXIT /B %RETVAL%
)
GOTO end

:error
SET RETVAL=1

:end
SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
