@echo off

if "%1 %2" == "git push" goto git_push
if "%1 %2" == "adb conn" goto adb_conn

:arg_error
echo error. Not a command
goto end

:git_push
git add .
git commit -m ".%3"
git push
goto end

:adb_conn
adb kill-server
adb start-server
adb tcpip 5555
adb connect 192.168.3.94:5555
goto end

:end
