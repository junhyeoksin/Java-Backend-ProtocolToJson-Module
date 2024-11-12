#!/bin/sh


JAVA_HOME=/usr/java/jdk1.8.0_202

HOMEDIR=/data/IBKSB.TXService
CLASSLIB=${HOMEDIR}/lib/commons-codec-1.14.jar
CLASSLIB=${CLASSLIB}:${HOMEDIR}/lib/commons-io-2.6.jar
CLASSLIB=${CLASSLIB}:${HOMEDIR}/lib/commons-logging-1.2.jar
CLASSLIB=${CLASSLIB}:${HOMEDIR}/lib/commons-net-3.6.jar
CLASSLIB=${CLASSLIB}:${HOMEDIR}/lib/log4j-api-2.17.0.jar
CLASSLIB=${CLASSLIB}:${HOMEDIR}/lib/log4j-core-2.17.0.jar
CLASSLIB=${CLASSLIB}:${HOMEDIR}/lib/log4j-jcl-2.17.0.jar
CLASSLIB=${CLASSLIB}:${HOMEDIR}/lib/log4j-slf4j-impl-2.17.0.jar
CLASSLIB=${CLASSLIB}:${HOMEDIR}/lib/slf4j-api-1.7.32.jar
CLASSLIB=${CLASSLIB}:${HOMEDIR}/lib/json-simple-1.1.1.jar


CLASSLIB=${CLASSLIB}:${HOMEDIR}/classes

MAIN_CLASS=ntree.txproxy.TXProxyServer
RUN_CLASS=TXProxyServer
PROXY_ID=$1
LOG_PATH=${HOMEDIR}/logs/$1.log

 

PIDFILE=${HOMEDIR}/bin/${PROXY_ID}.pid
if [ -e ${PIDFILE} ]; then
        PID=`cat ${PIDFILE}`
        kill -9 $PID
fi

# 새로운 프로세스 시작
${JAVA_HOME}/bin/java -cp ${CLASSLIB} -D${RUN_CLASS} -Dntree.home=${HOMEDIR} ${MAIN_CLASS} $* > ${LOG_PATH} 2>&1 & echo $! > ${PIDFILE}

