#!/usr/bin/env bash
if [ $# -gt 1 ]
then
   im_port=$1
   http_port=$2
   if [ ${im_port} -lt 8000 ] || [ ${im_port} -gt 20000 ] || [ ${http_port} -lt 8000 ] || [ ${http_port} -gt 20000 ]
   then
    echo '端口只能在 8000 ~ 20000 范围内'
    exit
   fi
else
    echo '使用方法：设置端口: <IM_PORT> <HTTP_PORT>'
    exit
fi
echo '启动Server，Socket端口 ['${im_port}'], HTTP端口 ['${http_port}']:'
nohup java -jar ry-server/target/ry-server-1.0.jar --server.port=${http_port} --server.im-port=${im_port} --logging.file=./logs/ry-server-${http_port}.log > /dev/null 2>&1 &
sleep 3
if [ $# == 2 ]
then
    tail -f ./logs/ry-server-${http_port}.log
fi
