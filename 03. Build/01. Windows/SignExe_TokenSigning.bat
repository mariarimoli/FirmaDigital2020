@echo off
echo Hello, World! Have a nice day!
rem this BATCH file will sign a Execution file
rem Usually, the SignTool is placed at "C:\Program Files\Microsoft SDKs\Windows\v7.0A\bin"
rem Don't forget to add this folder into PATH variable

rem Change this
set FileToSign="%cd%\Plugin\TokenSigning.exe"
set SignCert="TOKENSIGNING"
set StoreName="MY"
set TimeStampURL="http://timestamp.verisign.com/scripts/timstamp.dll"
set CrossCert="MSCV-VSClass3.cer"

echo Signing file: %FileToSign%
sign4j signtool sign /v /ph /s %StoreName% /i %SignCert% /t %TimeStampURL% %FileToSign%

set TokenSigningEnv="%cd%\Plugin\TokenSigningEnv.dll"
signtool sign /v /ph /s %StoreName% /i %SignCert% /t %TimeStampURL% %TokenSigningEnv%
set TokenSigningSetup="%cd%\Plugin\TokenSigningSetup.exe"
signtool sign /v /ph /s %StoreName% /i %SignCert% /t %TimeStampURL% %TokenSigningSetup%
rem signtool sign /v /ph /ac %CrossCert% /s %StoreName% /n %SignCert% /t %TimeStampURL% %FileToSign%
rem signtool verify /v %FileToVerify%

echo --Finished.
echo.
pause >nul