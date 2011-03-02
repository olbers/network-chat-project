@echo off
REM Start_Server.bat
REM NCP_Team
GOTO start

:start
java -jar NCP_Server.jar
GOTO checkErrLv

:checkErrLv
if %ERRORLEVEL%==5 GOTO start else GOTO end

:end
pause