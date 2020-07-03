@echo off
echo Hello, World! Have a nice day!
rem this BATCH file will sign a Execution file
rem Usually, the SignTool is placed at "C:\Program Files\Microsoft SDKs\Windows\v7.0A\bin"
rem Don't forget to add this folder into PATH variable

rem Change this
set FileToSign="%cd%\TOKEN SIGNING_Setup_1.0.0.6.exe"
set SignCert="TOKENSIGNING"
set StoreName="MY"
set TimeStampURL="http://timestamp.verisign.com/scripts/timstamp.dll"
set CrossCert="MSCV-VSClass3.cer"


echo Signing file: %FileToSign%

signtool sign /v /ph /s %StoreName% /i %SignCert% /t %TimeStampURL% %FileToSign%

echo --Finished.
echo.
pause >nul