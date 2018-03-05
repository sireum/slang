#!/bin/bash -e
rm -fR runtime mill versions.properties out
wget -q http://files.sireum.org/mill-standalone
chmod +x mill-standalone
wget -q https://raw.githubusercontent.com/sireum/v3/master/versions.properties
git clone --depth 1 https://github.com/sireum/runtime runtime
JAVA_OPTS="-Dorg.sireum.version.file=versions.properties" ./mill-standalone all slang.parser.shared.tests.test slang.frontend.shared.tests.test
