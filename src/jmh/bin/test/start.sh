#!/bin/bash
source /data/java/jmh/app/getConfValue.sh
function start(){
methodName=$1
thread=$2
warmupIteration=$(readConfig ${methodName} 'warmupIteration')
warmupTime=$(readConfig ${methodName} 'warmupTime')
iterations=$(readConfig ${methodName} 'iterations')
timeOnIteration=$(readConfig ${methodName} 'timeOnIteration')
resultsFile=$3
java -jar weidentity-java-sdk-jmh.jar ${methodName} -t ${thread} -f 1 -wi ${warmupIteration} -i ${iterations} -w ${warmupTime} -r ${timeOnIteration} -bm 'Throughput,AverageTime' -rf 'text' -rff ${resultsFile}
}
