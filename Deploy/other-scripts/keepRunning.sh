#!/bin/bash
while [[ true ]];
do
	echo $(date) >> /tmp/lasad.log
	java -Djava.security.policy=java.policy -jar LASAD-Server.jar server.cfg >> /tmp/lasad.log 2>&1
	sleep 5
done
