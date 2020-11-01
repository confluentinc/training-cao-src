export NS=io.confluent.monitoring.clients.interceptor
yum install -y libmnl
rpm -i --nodeps /config/iproute-tc-4.18.0-15.el8.x86_64.rpm
tc qdisc add dev eth0 root netem delay 50ms
kafka-console-consumer \
    --group group-with-6-partitions \
    --bootstrap-server kafka-1:9092 \
    --from-beginning \
    --topic test-topic-6 \
    --consumer-property "interceptor.classes=${NS}.MonitoringConsumerInterceptor" \
    --consumer-property "confluent.monitoring.interceptor.bootstrap.servers=kafka-1:9092" \
    --consumer-property "client.id=consumer-$1"
