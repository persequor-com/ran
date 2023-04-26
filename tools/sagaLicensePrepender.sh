#!/bin/bash
# This script prepends the Persequor copyright header to all java files which don't have it.
# It first checks if it's not already there so the operation is idempotent

# set -x pipefail

# set script name
me=`basename "$0"`
echo "Running $me script"

read -r -d '' HELP_MSG << EOM
Options:
    -l: search path to search recursively for *.java files to add license text to, it can be . (dot)
    -e: accepts everything, but only 'pipeline' will make the script's exit code equal to changed files. Useful in build systems were we expect no changes.
    -h: show help
Examples:
    ./$me -l . -e pipeline
    ./$me -l . (this is the same as ./$me -l . -e dev)
EOM

while getopts ":l:e:h" opt; do
  case $opt in
    l) RUN_LOCATION="$OPTARG";;
    e) RUN_ENVIRONMENT="$OPTARG";;
    h) echo "$HELP_MSG"; exit 0 >&2;;
    \?) echo "Unknown option -$OPTARG"; echo "$HELP_MSG"; exit 1 >&2;;
    :) echo "Missing option argument for -$OPTARG" >&2; exit 1;;
    *) echo "Unimplemented option: -$OPTARG" >&2; exit 1;;
  esac
done
if [[ -z $RUN_LOCATION ]]
then
    echo "ERROR: search path is mandatory - it can be . (dot)"
    echo "$HELP_MSG"
    exit 1
fi


licenseText="/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */"
 licenseTextStart="/* Copyright 2021 PSQR"

sagaLicensePrepend() {
  for file in "$1"/*
    do
      if [ "$file" == "./saga-app" ] || [ "$file" == "./saga-web" ]
      then :
      elif [ -d "$file" ]
      then
        sagaLicensePrepend "$file"
      fi
      if [[ $file == *.java ]]
      then
        if ! grep -q "$licenseTextStart" "$file"
        then
          echo $file
          NUMBER_OF_FILES_CHANGED_BY_PREPENDEDER=$((NUMBER_OF_FILES_CHANGED_BY_PREPENDEDER+1))
          echo "$licenseText" | cat - "$file" >> temp && mv temp "$file"
        fi
      fi
    done
}

NUMBER_OF_FILES_CHANGED_BY_PREPENDEDER=0
echo "Prepending license information"
  sagaLicensePrepend "$RUN_LOCATION"
echo "Done"
if [[ "$RUN_ENVIRONMENT" == "pipeline" && $NUMBER_OF_FILES_CHANGED_BY_PREPENDEDER -ne 0 ]]; then
    echo "####################### Failure ############################"
    echo "ERROR: In build pipeline mode we do not allow license header additions - you need to commit these with your other changes and run the license prepender tool before pushing to the build system."
    echo "############################################################"
  exit $NUMBER_OF_FILES_CHANGED_BY_PREPENDEDER
fi
