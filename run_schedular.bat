@echo off
REM project ni directory chhe 
cd "C:\projects\ExpenseManagementProject"

REM java no runtime no path
set JAVA_EXE="C:\Program Files\Java\jdk-22\bin\java.exe"

REM mail schedular ne run karva mate ni command
%JAVA_EXE% -cp ".;AutoMailScheduler.jar;lib/*" mailschedular.AutoMailSchedular

pause