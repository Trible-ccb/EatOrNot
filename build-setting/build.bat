@echo off

@echo the batch for build EatOrNot project
@echo create by EatOrNot-DevTeam
@echo 2011-07-25

@echo verify build environment 

@echo verify local android sdk dir
if %ANDROID_SDK% == "" (
	@echo please set your local android sdk dir
	goto :EOF
	)

if not "%1" == "" (
	call :build %1
) else (
	for /f "eol=# delims=" %%i in (./market.txt) do call :build %%i
)

:build
	if not "%1" == "" ( 
		if "%1" == "LePhone" (
		COPY /V /B /Y "..\build-resources\lephone_icon.png" "..\res\drawable-hdpi\icon.png" /B
		ant -buildfile auto-build.xml eatornot -Dmarket=%1
		COPY /B /Y "..\build-resources\default_hidp_icon.png" "..\res\drawable-hdpi\icon.png" /B
		) else (
		ant -buildfile auto-build.xml eatornot -Dmarket=%1
		)
	)
	goto :EOF
	
@echo build ok
pause