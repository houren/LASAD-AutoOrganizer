#!/bin/sh
#
# /etc/init.d/lasad -- startup script for LASAD server
#
### BEGIN INIT INFO
# Provides:          lasad
# Required-Start:    $local_fs $remote_fs $network mysql tomcat
# Required-Stop:     $local_fs $remote_fs $network mysql tomcat
# Should-Start:      $named
# Should-Stop:       $named
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Start LASAD.
# Description:       Start the LASAD server part.
### END INIT INFO

LASAD_HOME=/home/lasad/lasad/LASAD-Server

case "$1" in
start)
pushd $LASAD_HOME
    su lasad -c ./keepRunning.sh > /dev/null &
popd
    ;;
stop)
    killall -u lasad >/dev/null
    ;;
\*)
    echo "usage: $0 (start|stop)"
esac
