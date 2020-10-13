export NS=io.confluent.monitoring.clients.interceptor
yum install -y libmnl
rpm -i --nodeps iproute-tc-4.18.0-15.el8.x86_64.rpm
tc qdisc add dev eth0 root netem delay 50ms
kafka-console-consumer \
    --group group-with-1-partition \
    --bootstrap-server kafka-1:9092 \
    --from-beginning \
    --topic test-topic-1 \
    --consumer-property "interceptor.classes=${NS}.MonitoringConsumerInterceptor" \
    --consumer-property "client.id=consumer-$1"
