#!/bin/bash -e
wget -q http://files.sireum.org/mill
chmod +x mill
wget -q https://raw.githubusercontent.com/sireum/v3/master/versions.properties
git clone https://github.com/sireum/v3-runtime runtime
JAVA_OPTS="-Dorg.sireum.version.file=versions.properties" ./mill all slang.parser.shared.tests.test slang.tipe.shared.tests.test
