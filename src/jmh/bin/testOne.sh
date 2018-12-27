#!/bin/bash
. /etc/profile
methodName=$1
thread=$2
resultsFile=$3
warmupIteration=$4
warmupTime=$5
iterations=$6
timeOnIteration=$7
basedir=$8
cd ${basedir}/bin
java -jar ${basedir}/app/weidentity-java-sdk-jmh.jar ${methodName} -t ${thread} -f 1 -wi ${warmupIteration} -i ${iterations} -w ${warmupTime} -r ${timeOnIteration} -bm 'Throughput,AverageTime' -rf 'text' -rff  ${basedir}/report/${resultsFile}
echo 'thread-'$var 'warmupIteration-'${warmupIteration} 'warmupTime-'${warmupTime} 'iterations-'${iterations} 'timeOnIteration-'${timeOnIteration} >> ${basedir}'/report/JmhTestResult.txt'
cat ${basedir}/report/${resultsFile} >> ${basedir}'/report/JmhTestResult.txt'
rm -rf ${basedir}/report/${resultsFile}
echo -e >> ${basedir}/'report/JmhTestResult.txt'