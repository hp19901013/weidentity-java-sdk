#!/bin/bash
function readConfig(){
section=$1
key=$2
val=$(awk -F '=' '/\['$section'\]/{a=1}a==1&&$1~/'$key'/{print $2;exit}' '../conf/config.ini' | awk '{print $1}')
echo ${val}
}

function testOne(){
methodName=$1
thread=$2
resultsFile=$3
warmupIteration=$4
warmupTime=$5
iterations=$6
timeOnIteration=$7
java -jar ../app/weidentity-java-sdk-jmh.jar ${methodName} -t ${thread} -f 1 -wi ${warmupIteration} -i ${iterations} -w ${warmupTime} -r ${timeOnIteration} -bm 'Throughput,AverageTime' -rf 'text' -rff ${resultsFile}
}

function multiThread(){
methodName=$1
threads=$(readConfig ${methodName} 'threads')
warmupIteration=$(readConfig ${methodName} 'warmupIteration')
warmupTime=$(readConfig ${methodName} 'warmupTime')
iterations=$(readConfig ${methodName} 'iterations')
timeOnIteration=$(readConfig ${methodName} 'timeOnIteration')
if [ ! -d "../report" ]; then
 mkdir ../report
 touch ../report/JmhTestResult.txt
fi
echo '['${methodName}']' >> '../report/JmhTestResult.txt'
OLD_IFS="$IFS"
IFS=","
array=($threads)
IFS="$OLD_IFS"
for var in ${array[@]}
do
   testOne ${methodName} $var ${methodName}-$var'thread.txt' ${warmupIteration} ${warmupTime} ${iterations} ${timeOnIteration}
   mv ${methodName}-$var'thread.txt' ../report
   echo 'thread-'$var 'warmupIteration-'${warmupIteration} 'warmupTime-'${warmupTime} 'iterations-'${iterations} 'timeOnIteration-'${timeOnIteration} >> '../report/JmhTestResult.txt'
   cat ../report/${methodName}-$var'thread.txt' >> '../report/JmhTestResult.txt'
   rm -rf ../report/${methodName}-$var'thread.txt'
   echo -e >> '../report/JmhTestResult.txt'
done
   echo -e -e >> '../report/JmhTestResult.txt'
}

function start(){
if [ ! -d "../report" ]; then
  mkdir ../report
fi
if [ ! -d "../report/old_result" ]; then
  mkdir ../report/old_result
fi
if [ -e "../report/JmhTestResult.txt" ]; then
  time=$(date +"%Y-%m-%d-%H-%M")
  mv ../report/JmhTestResult.txt ../report/old_result/JmhTestResult-${time}.bak
fi
rm -rf ../report/*.txt
methodNames=$(awk '/^\[\w*\]$/ {print $1}' ../conf/config.ini | awk -F '[' '{print $2}' | awk -F ']' '{print $1}')
OLD_IFS="$IFS"
IFS=" "
array=($methodNames)
IFS="$OLD_IFS"
for var in ${array[@]}
do
   isTest=$(readConfig $var 'isTest')
   if [ $isTest == 'true' ]
    then
	multiThread $var
   fi
done
}
start
