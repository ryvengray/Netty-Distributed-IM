#!/usr/bin/env bash
JAVA_OPTS='-Xmx128m -Xms32m -Xmn32m -XX:SurvivorRatio=8 -XX:MetaspaceSize=16m -XX:MaxMetaspaceSize=128m -XX:MaxTenuringThreshold=15'
http_port=8768
if [ $# -gt 0 ]
then
    if [ $1 = 'restart' ]
    then
        echo 'Eureka Stop...'
        pid=`ps -ef|grep ry-eureka|grep ${http_port}|awk '{print $2}'`
        if [ ${pid} -gt 0 ]
        then
            echo 'kill -15 '${pid}
            kill -15 ${pid}
            sleep 3
        else
            echo '没有找到已存在的进程'
        fi
    else
        echo '参数支持: restart'
        exit
    fi
fi
echo 'Eureka starting... port: ['${http_port}']:'
nohup java ${JAVA_OPTS} -jar ry-eureka/target/ry-eureka-1.0.jar --server.port=${http_port} --logging.file=./logs/ry-eureka-${http_port}.log > /dev/null 2>&1 &
sleep 3
tail -f ./logs/ry-eureka-${http_port}.log
