@echo off
cd ..
Set ROOT=%CD%
cd src
Set LIBS=%ROOT%\bin;%ROOT%\lib\QTJava.zip;%ROOT%\lib\derby.jar;%ROOT%\lib\jxl.jar

echo "java -cp %LIBS% mixnfix.bin.MIXnFIX db data"
java -cp %LIBS% mixnfix.bin.MIXnFIX db data