@ECHO OFF
set /p name=������汾���ƣ�
set /p code=������汾�ţ�
set /p last_name=��������һ�汾���ƣ�
set /p last_code=��������һ�汾�ţ�
echo %name% %code% %last_name% %last_code%
echo �������һ����� bsdiff XhuSchedule-%last_name%-%last_code%-master-release.apk XhuSchedule-%name%-%code%-master-release.apk %last_code%-%code%.patch
bash
certutil -hashfile .\XhuSchedule-%name%-%code%-master-release.apk MD5
echo 
certutil -hashfile .\%last_code%-%code%.patch MD5
pause