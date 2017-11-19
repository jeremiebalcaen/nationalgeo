@ECHO ON

SET ProgramPath=D:\programs\PhotoOfTheDay
SET ProxyHost=10.7.80.40
SET ProxyPort=8080

:::: SET YYYYMMDD format for date
FOR /F "TOKENS=1-3 DELIMS=- " %%A IN ("%DATE%") DO SET "DT=%%C%%B%%A"

:SetCheckFile
SET CheckDir=%ProgramPath%
SET checkfile=%CheckDir%\VersionBackup_%DT%.txt
IF NOT EXIST "%CheckDir%" MD "%CheckDir%"
IF EXIST "%checkfile%" GOTO EOF
ECHO Creating the check file for date %DT% to ensure only one Version Backup run for the day>>"%checkfile%"

:StartApp
powershell.exe "& '%ProgramPath%\wallpaper.ps1' '%ProgramPath%' %ProxyHost% %ProxyPort%"
PAUSE
GOTO EOF


