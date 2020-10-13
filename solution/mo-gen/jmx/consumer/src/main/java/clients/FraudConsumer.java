package clients;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

import io.confluent.ksql.avro_schemas.KsqlDataSourceSchema;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class FraudConsumer
 {
    public static void main(String[] args) {
        System.out.println(">>> Starting Sample Avro Consumer Application");

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "fraud-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        // Use Specific Record or else you get Avro GenericRecord.
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        props.put("schema.registry.url", "http://schema-registry:8081");

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        final KafkaConsumer<String, KsqlDataSourceSchema> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("CC_POTENTIAL_FRAUD_COUNTS"));

        try {
            while (true) {
                ConsumerRecords<String, KsqlDataSourceSchema> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, KsqlDataSourceSchema> record : records) {
                    System.out.printf("%s: %s \n", record.value().getCREDITCARDNBR(), record.value().getATTEMPTS());
                }
            }
        } finally {
            consumer.close();
        }
    }
 }