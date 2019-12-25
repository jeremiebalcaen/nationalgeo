@ECHO ON

SET ProgramPath=C:\Program Files\PhotoOfTheDay
SET downloadPath=C:\projects\nationalgeo\download
SET archive=false
rem SET ProxyHost=10.186.3.69
rem SET ProxyPort=8080

:::: SET YYYYMMDD format for date
FOR /F "TOKENS=1-3 DELIMS=/ " %%A IN ("%DATE%") DO SET "DT=%%C%%B%%A"

:SetCheckFile
SET CheckDir=%downloadPath%
SET checkfile=%CheckDir%\outputlog_%DT%.txt
IF NOT EXIST "%CheckDir%" MD "%CheckDir%"

IF EXIST "%checkfile%" (
	set archive=true
	GOTO :StartApp
)
ELSE ECHO Creating the check file for date %DT% to ensure only one Version Backup run for the day>>"%checkfile%"

:StartApp
powershell.exe "& '%ProgramPath%\wallpaper.ps1' '%ProgramPath%' '%downloadPath%' '%checkfile%' %archive% %ProxyHost% %ProxyPort%"

PAUSE
GOTO EOF
