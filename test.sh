#!/bin/bash -e
rm -fR runtime mill versions.properties out
wget -q http://files.sireum.org/mill
chmod +x mill
wget -q https://raw.githubusercontent.com/sireum/v3/master/versions.properties
git clone --depth 1 https://github.com/sireum/runtime runtime
TERM=xterm-color JAVA_OPTS="-Dorg.sireum.version.file=versions.properties" ./mill all slang.parser.shared.tests.test slang.tipe.shared.tests.test
