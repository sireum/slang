#!/bin/bash -e
export SCRIPT_HOME=$( cd "$( dirname "$0" )" &> /dev/null && pwd )
cd ${SCRIPT_HOME}
if [ ! -e mill-standalone ]; then
  curl -c /dev/null -Lo mill-standalone http://files.sireum.org/mill-standalone
  chmod +x mill-standalone
fi
if [ ! -e versions.properties ]; then
  curl -Lo versions.properties https://raw.githubusercontent.com/sireum/kekinian/master/versions.properties
fi
rm -fR runtime
git clone --depth=1 https://github.com/sireum/runtime
