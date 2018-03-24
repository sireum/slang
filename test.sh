#!/bin/bash -e
rm -fR runtime mill-standalone versions.properties out
curl -Lo mill-standalone http://files.sireum.org/mill-standalone
chmod +x mill-standalone
curl -Lo versions.properties https://raw.githubusercontent.com/sireum/kekinian/master/versions.properties
git clone --depth 1 https://github.com/sireum/runtime runtime
./mill-standalone all slang.parser.shared.tests.test slang.frontend.shared.tests.test
