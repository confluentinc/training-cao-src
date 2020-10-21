export NS=io.confluent.monitoring.clients.interceptor
yum install -y libmnl
rpm -i --nodeps /config/iproute-tc-4.18.0-15.el8.x86_64.rpm
tc qdisc add dev eth0 root netem delay 10ms
kafka-console-consumer \
    --group group-with-default-settings \
    --bootstrap-server kafka-1:9092 \
    --from-beginning \
    --topic tuning-topic \
    --consumer-property "interceptor.classes=${NS}.MonitoringConsumerInterceptor" \
    --consumer-property "confluent.monitoring.interceptor.bootstrap.servers=kafka-1:9092" \
    --consumer-property "client.id=default-settings-consumer"
