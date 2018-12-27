#!/bin/bash
. /etc/profile
basedir=$1
if [ ! -d ${basedir}/report ]; then
    mkdir ${basedir}/report
fi
if [ ! -d ${basedir}/report/old_result ]; then
    mkdir ${basedir}/report/old_result
fi
if [ -f ${basedir}/report/JmhTestResult.txt ]; then
    time=$(date +"%Y-%m-%d-%H-%M")
mv ${basedir}/report/JmhTestResult.txt ${basedir}/report/old_result/JmhTestResult-${time}.bak
fi
rm -rf ${basedir}/report/*.txt