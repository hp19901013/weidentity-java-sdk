#!/bin/bash
function many_method(){
methodNames=$(awk '/^\[\w*\]$/ {print $1}' ../conf/config.ini | awk -F '[' '{print $2}' | awk -F ']' '{print $1}')
OLD_IFS="$IFS"
IFS=" "
array=($methodNames)
IFS="$OLD_IFS"
for var in ${array[@]}
do
   isTest=$(readConfig $var 'isTest')
    echo $isTest
done
}