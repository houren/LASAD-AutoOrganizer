#!/bin/bash
homeDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
stopScript=${homeDir}"/stopLASAD.sh"
startScript=${homeDir}"/startLASAD.sh"

echo "Rebooting LASAD"
lasadStop=$stopScript
$lasadStop

declare -i i=0

while [[ $(ps ax | grep '[L]ASAD-Server.jar' | awk '{print $1}') =~ ^[0-9]+$ ]]
do
    i=$((i+1))
    sleep 2
    if [ ! $i -lt 10 ]
    then
        break
    fi
done

lasadStart=$startScript
$lasadStart

declare -i j=0

while true
do
    if [[ $(ps ax | grep '[L]ASAD-Server.jar' | awk '{print $1}') =~ ^[0-9]+$ ]]
    then
        echo "SUCCESSFUL reboot of LASAD"
        break
    else
        j=$((j+1))
        sleep 2
        if [ ! $j -lt 10 ]
        then
            echo "FAILED reboot of LASAD"
            break
        fi
    fi
done
