#!/bin/bash
function readConfig(){
    section=$1
    key=$2
    basedir=$(dirname "$PWD")
    val=$(awk -F '=' '/\['$section'\]/{a=1}a==1&&$1~/'$key'/{print $2;exit}' ${basedir}'/conf/config.ini' | awk '{print $1}')
    echo ${val}
}

function multiThread(){
    basedir=$(dirname "$PWD")
    methodName=$1
    threads=$(readConfig ${methodName} 'threads')
    warmupIteration=$(readConfig ${methodName} 'warmupIteration')
    warmupTime=$(readConfig ${methodName} 'warmupTime')
    iterations=$(readConfig ${methodName} 'iterations')
    timeOnIteration=$(readConfig ${methodName} 'timeOnIteration')

    sh ${basedir}/bin/checkResultFileIsExit.sh ${basedir}
    ssh app@10.107.105.203 "sh ${basedir}/bin/checkResultFileIsExit.sh ${basedir}"

    echo '['${methodName}']' >> ${basedir}'/report/JmhTestResult.txt'
    ssh app@10.107.105.203 "echo '['${methodName}']' >> ${basedir}'/report/JmhTestResult.txt'"

    OLD_IFS="$IFS"
    IFS=","
    array=($threads)
    IFS="$OLD_IFS"
    for var in ${array[@]}
    do
        ssh app@10.107.105.203 "sh ${basedir}/bin/testOne.sh ${methodName} $var ${methodName}-$var'thread.txt' ${warmupIteration} ${warmupTime} ${iterations} ${timeOnIteration} ${basedir} >> ${basedir}/bin/console.log &"
        sh ${basedir}/'bin/testOne.sh' ${methodName} $var ${methodName}-$var'thread.txt' ${warmupIteration} ${warmupTime} ${iterations} ${timeOnIteration} ${basedir}
        sleep 60
    done
   echo -e -e >> ${basedir}'/report/JmhTestResult.txt'
   ssh app@10.107.105.203 "echo -e -e >> ${basedir}'/report/JmhTestResult.txt'"
}

function start(){
basedir=$(dirname "$PWD")

    sh ${basedir}/bin/beforeTestStart.sh ${basedir}
    ssh app@10.107.105.203 "sh ${basedir}/bin/beforeTestStart.sh ${basedir}"

    methodNames=$(awk '/^\[\w*\]$/ {print $1}' ${basedir}/conf/config.ini | awk -F '[' '{print $2}' | awk -F ']' '{print $1}')
    OLD_IFS="$IFS"
    IFS=" "
    array=($methodNames)
    IFS="$OLD_IFS"
    for var in ${array[@]};
    do
        isTest=$(readConfig $var 'isTest')
        if [ $isTest == 'true' ];then
            multiThread $var
        fi
    done
}
start