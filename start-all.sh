#!/usr/bin/env bash
eureka_port=(8768 'ry-eureka')
server_port=(8900 'ry-server')
user_port=(8765 'ry-user')
route_port=(8888 'ry-route')
all_ports=(${eureka_port[0]} ${server_port[0]} ${user_port[0]} ${route_port[0]})
all_services=(${eureka_port[1]} ${server_port[1]} ${user_port[1]} ${route_port[1]})
mvn clean install -DskipTests
rc=$?
if [[ ${rc} -ne 0 ]] ; then
    print "[FAIL] maven package error"
    exit ${rc}
fi
echo 'Shutdown services...'
for var in ${all_ports[@]}
do
    # shutdown
    curl -X POST http://localhost:${var}/actuator/shutdown
    sleep 1
done
echo 'Shutdown end'

for var in ${all_services[@]}
do
    echo 'Start '${var}'...'
    # shutdown
    nohup java -jar ${var}/target/${var}-1.0.jar --logging.file=./logs/${var}.log > /dev/null 2>&1 &
    echo 'Start '${var}' End'
    sleep 1
done

echo 'All end. Visit http://localhost:8888/login.html'
