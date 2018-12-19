#!/bin/bash
function readConfig(){
section=$1
key=$2
val=$(awk -F '=' '/\['$section'\]/{a=1}a==1&&$1~/'$key'/{print $2;exit}' '../conf/config.ini' | awk '{print $1}')
echo ${val}
}
