#!/bin/bash
# Metroaccess database generation script for Linux and MaxOS X
# (C) Dmitry Valetin aka suntehnik
# Download and compile JNI library from http://www.ch-werner.de/javasqlite/

if [ $# -ne 1 ] 
then
	echo "Usage: $0 <path to direcotry with CSV files>"
else 
	ARCH=`uname -s`
	if [ $ARCH == "Darwin" ]
	then 
		SQLITE_JNI_LIB_PATH=/opt/local/lib/
	else
		SQLITE_JNI_LIB_PATH=/usr/local/lib/
	fi
	java -DSQLite.library.path=$SQLITE_JNI_LIB_PATH  -jar dbgen.jar $1 
fi
