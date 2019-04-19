#!/usr/bin/env bash
ROUTE_PORT=9412
mvn clean install -DskipTests
rc=$?
if [[ ${rc} -ne 0 ]] ; then
    echo "[FAIL] maven package error"
    exit ${rc}
fi
 kill
# 使用kill -15 优雅关闭
all_pid=`ps -ef|grep 'java -jar ry-'|grep 'server.port'| awk '{print $2}'`
for var in ${all_pid}
do
    kill -15 ${var}
done
all_pid=`ps -ef|grep 'java -jar ry-'|grep 'server.port'| awk '{print $2}'`
echo 'Rest Pid: '${all_pid}
jar-start.sh ry-eureka 8768
echo '启动 Ry-Eureka...'
sleep 3
jar-start.sh ry-config-server 9501
echo '启动 Ry-Config-Server...'
sleep 10
jar-start.sh ry-user 9411
echo '启动 Ry-User...'
server-add.sh 10010 9418 'n'
echo '启动 Ry-Server...'
sleep 3
jar-start.sh ry-route ${ROUTE_PORT}
echo '启动 Ry-Route...'
sleep 3
echo '访问 http://localhost:'${ROUTE_PORT}'/login.html'