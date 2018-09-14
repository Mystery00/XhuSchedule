@ECHO OFF
set /p name=请输入版本名称：
set /p code=请输入版本号：
set /p last_name=请输入上一版本名称：
set /p last_code=请输入上一版本号：
echo %name% %code% %last_name% %last_code%
echo 请键入这一行命令： bsdiff XhuSchedule-%last_name%-%last_code%-master-release.apk XhuSchedule-%name%-%code%-master-release.apk %last_code%-%code%.patch
bash
certutil -hashfile .\XhuSchedule-%name%-%code%-master-release.apk MD5
echo 
certutil -hashfile .\%last_code%-%code%.patch MD5
pause