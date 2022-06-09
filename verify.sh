#!/usr/bin/env bash

if ! command -v java &> /dev/null 
then
  echo "ERROR: could not locate java"
  exit 1
else
  echo -n "SUCCESS: Java v$(java -version 2>&1 | awk -F '"' 'NR==1 {print $2}') installed"
fi

if ! command -v clj &> /dev/null
then
  echo "ERROR: could not locate clojure cli" >&2
  exit 1
else
  echo "SUCCESS: Clojure CLI v$(clj --version | awk -F 'version ' 'NR==1 {print $2}') installed"
fi

if ! command -v lein &> /dev/null 
then
  echo "ERROR: could not locate leiningen" >&2
  exit 1
else
  echo "SUCCESS: $(lein --version | awk -F ' on' 'NR==1 {print $1}') installed"
fi

if ! command -v node &> /dev/null
then
  echo "ERROR: could not locate node" >&2
  exit 1
else
  echo "SUCCESS: Node $(node --version) installed"
fi

if ! command -v npm &> /dev/null
then
  echo "ERROR: could not locate npm" >&2
  exit 1
else
  echo "SUCCESS: NPM v$(npm --version) installed"
fi


if ! command -v firebase &> /dev/null
then
  echo "ERROR: could not locate firebase emulator." >&2
  exit 1
else
  echo "SUCCESS: Firebase emulator v$(firebase --version) installed"
fi

if ! command -v docker &> /dev/null
then
  echo "ERROR: could not locate docker" >&2
  exit 1
else
  echo "SUCCESS: $(docker --version) installed"
fi

lein deps
docker pull alekcz/firetomic:latest
