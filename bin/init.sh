#!/bin/bash -e
export SCRIPT_HOME=$( cd "$( dirname "$0" )" &> /dev/null && pwd )
cd ${SCRIPT_HOME}
curl -JLso prelude.sh https://raw.githubusercontent.com/sireum/kekinian/master/bin/prelude.sh
curl -JLso platform.sh https://raw.githubusercontent.com/sireum/kekinian/master/bin/platform.sh
chmod +x prelude.sh platform.sh
exec ./prelude.sh
