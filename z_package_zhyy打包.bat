set "projectName=iotpt-zhyy"

call mvn clean package -pl %projectName% -am

cd %projectName%/target

set "hour=%time:~0,2%"
if %hour% lss 10 (
    set "hour=0%time:~1,1%"
)

set "minute=%time:~3,2%"
if %minute% lss 10 (
    set "minute=0%time:~4,1%"
)

set "second=%time:~6,2%"
if %second% lss 10 (
    set "second=0%time:~7,1%"
)

set "datetimeformat=%date:~0,4%%date:~5,2%%date:~8,2%%hour%%minute%%second%"

set "filePath=%~dp0%projectName%\target\%projectName%-0.0.1"

echo %~dp0%projectName%\target\%projectName%-0.0.1
echo %filePath%
rem "C:\Program Files (x86)\360\360zip\360zip.exe" -ar %filePath% %filePath%%datetimeformat%.zip
"C:\Program Files (x86)\360\360zip\360zip.exe" -ar %filePath% %~dp0%projectName%%datetimeformat%.zip
