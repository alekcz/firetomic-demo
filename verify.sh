#!/usr/bin/env bash

echo "SUCCESS: Clojure CLI v$(clj --version | awk -F 'version ' 'NR==1 {print $2}') installed" || echo "ERROR: could not locate clojure cli"
echo "SUCCESS: Java v$(java -version 2>&1 | awk -F '"' 'NR==1 {print $2}') installed" || echo "ERROR: could not locate java"
echo "SUCCESS: $(lein --version | awk -F ' on' 'NR==1 {print $1}') installed" || echo "ERROR: could not locate leiningen"
echo "SUCCESS: Node $(node --version) installed" || echo "ERROR: could not locate nodejs"
echo "SUCCESS: NPM v$(npm --version) installed" || echo "ERROR: could not locate npm"
echo "SUCCESS: Firebase emulator v$(firebase --version) installed" || echo "ERROR: could not locate firebase emulator."
echo "SUCCESS: $(docker --version) installed" || echo "ERROR: could not locate docker"
lein deps