@powershell -noprofile -executionpolicy bypass -command Invoke-WebRequest -Uri https://raw.githubusercontent.com/sireum/kekinian/master/bin/init.bat -OutFile %~dp0prelude.bat
@%~dp0prelude.bat