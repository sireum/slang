#!/bin/bash -e
export SCRIPT_HOME=$( cd "$( dirname "$0" )" &> /dev/null && pwd )
cd ${SCRIPT_HOME}
download() {
  if hash curl 2>/dev/null; then
    curl -c /dev/null -JLso $1 $2
  elif hash wget 2>/dev/null; then
    wget -qO $1 $2
  else
    echo "Either curl or wget is required, but none found."
    exit 1
  fi
}
$(download prelude.sh https://raw.githubusercontent.com/sireum/kekinian/master/bin/init.sh)
chmod +x prelude.sh
exec ./prelude.sh
