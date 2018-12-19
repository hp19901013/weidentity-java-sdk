#!/bin/bash
source /data/java/jmh/app/multiThread.sh
function many_method(){
rm -rf ../report/*
methodNames=$(awk '/^\[\w*\]$/ {print $1}' ../conf/config.ini | awk -F '[' '{print $2}' | awk -F ']' '{print $1}')
OLD_IFS="$IFS"
IFS=" "
array=($methodNames)
IFS="$OLD_IFS"
for var in ${array[@]}
do
 # test_threads $var
   isTest=$(readConfig $var 'isTest')
   if [ $isTest == 'true' ]
    then
	test_threads $var
   fi
done
}
many_method
