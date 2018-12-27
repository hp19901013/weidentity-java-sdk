#!/bin/bash
. /etc/profile
basedir=$1
if [ ! -d ${basedir}/report ];then
    mkdir ${basedir}/report
fi
if [ ! -f ${basedir}/report/JmhTestResult.txt ];then
    touch ${basedir}/report/JmhTestResult.txt
fi