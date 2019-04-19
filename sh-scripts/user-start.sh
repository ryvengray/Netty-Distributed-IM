#!/usr/bin/env bash
http_port=9222
if [ $# -gt 0 ]
then
    if [ $1 = 'restart' ]
    then
        echo 'User Stop...'
        pid=`ps -ef|grep ry-user|grep server.port|awk '{print $2}'`
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
echo 'User starting... port: ['${http_port}']:'
nohup java -jar ry-user/target/ry-user-1.0.jar --server.port=${http_port} --logging.file=./logs/ry-user-${http_port}.log > /dev/null 2>&1 &
sleep 3
tail -f ./logs/ry-user-${http_port}.log
