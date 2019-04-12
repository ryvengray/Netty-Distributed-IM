#!/usr/bin/env bash
ROUTE_PORT=9412
mvn clean install -DskipTests
rc=$?
if [[ ${rc} -ne 0 ]] ; then
    print "[FAIL] maven package error"
    exit ${rc}
fi
 kill
all_ports=`ps -ef|grep 'java -jar ry-'|grep 'server.port'| awk '{print $11}'|awk -F= '{print $2}'`
for var in ${all_ports}
do
    curl -X POST http://localhost:${var}/actuator/shutdown
    sleep 1
done
# 如果没有清楚干净，使用kill
all_pid=`ps -ef|grep 'java -jar ry-'|grep 'server.port'| awk '{print $2}'`
for var in ${all_pid}
do
    kill -9 ${var}
done
all_pid=`ps -ef|grep 'java -jar ry-'|grep 'server.port'| awk '{print $2}'`
echo 'Rest Pid: '${all_pid}
./jar-start.sh ry-eureka 8768
echo '启动 Ry-Eureka...'
sleep 3
./jar-start.sh ry-user 9411
echo '启动 Ry-User...'
./server-add.sh 10010 9418 'n'
echo '启动 Ry-Server...'
sleep 3
./jar-start.sh ry-route ${ROUTE_PORT}
echo '启动 Ry-Route...'
sleep 3
echo '访问 http://localhost:'${ROUTE_PORT}'/login.html'