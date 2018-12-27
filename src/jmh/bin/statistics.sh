#!/bin/bash
function getServerData(){
    serverIp=$1
    method=$2
    threadNum=$3
    sleepTime=$4
    sleep ${sleepTime}
    basedir=$(dirname "$PWD")
    if [ ! -d "${basedir}/report/serverData" ]; then
        mkdir ${basedir}/report/serverData
    fi
    if [ ! -d "${basedir}/report/serverData/${serverIp}" ]; then
        mkdir ${basedir}/report/serverData/${serverIp}
    fi
    ssh app@${serverIp} 'sar -u 2 10' >> ${basedir}/report/serverData/${serverIp}/${method}-${threadNum}thread-cpu.txt &
    ssh app@${serverIp} 'sar -d 2 10 -p' >> ${basedir}/report/serverData/${serverIp}/${method}-${threadNum}thread-disk.txt &
    ssh app@${serverIp} 'sar -r 2 10 -h' >> ${basedir}/report/serverData/${serverIp}/${method}-${threadNum}thread-memory.txt &
    ssh app@${serverIp} 'sar -n DEV 2 10 -h' >> ${basedir}/report/serverData/${serverIp}/${method}-${threadNum}thread-bandWidth.txt &
}
getServerData $1 $2 $3 &
