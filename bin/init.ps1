Invoke-WebRequest -Uri "https://raw.githubusercontent.com/sireum/kekinian/master/bin/init.ps1" -OutFile "$PSScriptRoot\prelude.ps1"
& "$PSScriptRoot\prelude.ps1"