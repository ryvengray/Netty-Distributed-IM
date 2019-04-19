#!/usr/bin/env bash
JAVA_OPTS='-Xmx128m -Xms32m -Xmn32m -XX:SurvivorRatio=8 -XX:MetaspaceSize=16m -XX:MaxMetaspaceSize=128m -XX:MaxTenuringThreshold=15'
if [ $# -gt 1 ]
then
   server_name=$1
   http_port=$2
   if [ ${http_port} -lt 8000 ] || [ ${http_port} -gt 20000 ]
   then
    echo '端口只能在 8000 ~ 20000 范围内'
    exit
   fi
   # 判断server_name 存在与否
   if [ ! -f ${server_name}'/target/'${server_name}'-1.0.jar' ]
   then
    echo '服务 【'${server_name}'】 不存在，请使用module文件夹名，如 ry-user、ry-route'
    exit
   fi
else
    echo '使用方法：设置端口: <SERVER_NAME> <HTTP_PORT>。如: ./jar-start.sh ry-eureka 8989'
    exit
fi
echo '首先关闭端口 ['${http_port}']'
curl -X POST http://localhost:${http_port}/actuator/shutdown
echo '启动Server, HTTP端口 ['${http_port}']:'
nohup java ${JAVA_OPTS} -jar ${server_name}/target/${server_name}-1.0.jar --server.port=${http_port} --logging.file=./logs/${server_name}-${http_port}.log > /dev/null 2>&1 &
sleep 3
##tail -f ./logs/${server_name}r-${http_port}.log

