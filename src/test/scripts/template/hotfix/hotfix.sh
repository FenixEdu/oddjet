#!/bin/bash
cd  ../../../../main/scripts/template/hotfix/
#XXX file names should not have spaces in them! This script will interpret them as different files.
for file in `ls ../../../../test/resources | grep -E 'odt|ott'`
do
echo "Fixing" "$file" "Template" 
./hotfix.sh ../../../../test/resources "$file"
done
