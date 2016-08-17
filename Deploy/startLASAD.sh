#!/bin/bash

homeDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

tomcatDir="${homeDir}/apache-tomcat-8.0.23"
tomcatStartup="/bin/./startup.sh"

lasadServerPid=$(ps ax | grep '[L]ASAD-Server.jar' | awk '{print $1}')

if [[ "$lasadServerPid"  =~ ^[0-9]+$ ]]
then
    echo "Server already running, ID: $lasadServerPid"
else
    cd ${homeDir}/lasad-server

    echo "Starting server from ${PWD}"
    java -Xmx6143m -Djava.security.policy=java.policy -jar LASAD-Server.jar server.cfg >> server.log 2>&1&
fi

lasadServerPid=$(ps ax | grep '[L]ASAD-Server.jar' | awk '{print $1}')
if [[ "$lasadServerPid"  =~ ^[0-9]+$ ]]
then
    echo "Server launched as PID: $lasadServerPid"
    echo "pausing before starting tomcat"
    sleep 5
    tomcatStartup=$tomcatDir$tomcatStartup
    echo "Starting tomcat"
    $tomcatStartup
else
    echo "ERROR: Failed to start server"
fi
