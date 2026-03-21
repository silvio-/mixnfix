@echo off
cd ..
Set ROOT=%CD%
cd src
Set BUILD=%ROOT%\build
Set DERBY_HOME=%ROOT%\lib\derby

echo %ROOT%

Set LIBS=%ROOT%\lib\QTJava.zip;%ROOT%\lib\derby.jar;%ROOT%\lib\jxl.jar

java -jar %DERBY_HOME%\lib\derbyrun.jar ij script

setlocal EnableDelayedExpansion
del file.list
for /L %%n in (1 1 500) do if "!__cd__:~%%n,1!" neq "" set /a "len=%%n+1"
setlocal DisableDelayedExpansion
for /r . %%g in (*.java) do (
  set "absPath=%%g"
  setlocal EnableDelayedExpansion
  set "relPath=!absPath:~%len%!"
  echo(!relPath!
  endlocal
)>>file.list

mkdir %ROOT%\bin
javac -encoding utf-8 -d %ROOT%\bin -sourcepath %ROOT%\src -cp %LIBS% @file.list
