#!/bin/bash

source /data/java/jmh/app/start.sh
function test_threads(){
methodName=$1
threads=$(readConfig ${methodName} 'threads')
OLD_IFS="$IFS"
IFS=","
array=($threads)
IFS="$OLD_IFS"
for var in ${array[@]}
do
   start ${methodName} $var ${methodName}-$var'thread.txt'
   if [ ! -d "../report" ]; then
     mkdir ../report
     touch ../report/JmhTestResult.txt
   fi
   mv ${methodName}-$var'thread.txt' ../report
   echo ${methodName}-$var'thread.txt' >> '../report/JmhTestResult.txt'
   cat ../report/${methodName}-$var'thread.txt' >> '../report/JmhTestResult.txt'
done
   echo -e >> '../report/JmhTestResult.txt'

}
