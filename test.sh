#!/bin/bash -e
export SCRIPT_HOME=$( cd "$( dirname "$0" )" &> /dev/null && pwd )
cd ${SCRIPT_HOME}
./prelude.sh
rm -fR out
./mill-standalone all slang.parser.shared.tests.test slang.frontend.shared.tests.test
