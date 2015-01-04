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


call :build


:build

	ant -buildfile auto-build.xml eatornot -Dmarket="original"

	goto :EOF
	
@echo build ok
pause