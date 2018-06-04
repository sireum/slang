#!/bin/bash -e
export SCRIPT_HOME=$( cd "$( dirname "$0" )" &> /dev/null && pwd )
rm -fR runtime mill-standalone versions.properties out
curl -Lo mill-standalone http://files.sireum.org/mill-standalone
chmod +x mill-standalone
curl -Lo versions.properties https://raw.githubusercontent.com/sireum/kekinian/master/versions.properties
git clone --depth 1 https://github.com/sireum/runtime runtime
$SCRIPT_HOME/mill-standalone all slang.parser.shared.tests.test slang.frontend.shared.tests.test
