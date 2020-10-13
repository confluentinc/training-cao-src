package clients;

import java.lang.Math;
import java.lang.InterruptedException;;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

import clients.model.AuthorizationValue;

public class AuthorizationProducer {
    List<String> _creditCards;
    KafkaProducer<String,Object> _producer;
    final String _topicName = getEnvrionmentValue("TOPIC_NAME", "cc-authorization-topic");
    final int NUMBER_OF_CC = 10000;

    private String getEnvrionmentValue(String key, String defaultValue){
        String value = System.getenv(key);
        return value == null ? defaultValue : value;
    }

    private KafkaProducer<String,Object> createProducer() throws IOException {
        Properties props = new Properties();
    	InputStream input = new FileInputStream("/app/producer.properties");
        props.load(input);
        KafkaProducer<String, Object> producer = new KafkaProducer<>(props);
        return producer;
    }

    private List<String> getCreditCards(){
        if(_creditCards == null){
            _creditCards = new ArrayList<String>();
            for(int i=0; i<NUMBER_OF_CC; i++){
                _creditCards.add(String.format("cc-a0%09d", i));
            }
        }
        return _creditCards;
    }

    private AuthorizationValue getAuthorizatioValue(String ccNbr, String status){
        Date date= new Date();
        long probeTime = date.getTime();
        return new AuthorizationValue(ccNbr, probeTime, status);
    }

    private void publishAuthorization(String ccNbr, String status){
        String key = ccNbr;
        AuthorizationValue value = getAuthorizatioValue(ccNbr, status);
        ProducerRecord<String, Object> record = new ProducerRecord<>(_topicName, key, value);
        _producer.send(record);
    }

    private void publishAuth(String ccNbr, String status, int num, String text) throws InterruptedException{
        for(int i=0; i<num; i++){
            publishAuthorization(ccNbr, status);
            TimeUnit.MILLISECONDS.sleep(100);
        }
        System.out.print(text);
    }

    private void produce() throws IOException, InterruptedException{
        _producer = createProducer();
        _creditCards = getCreditCards();
        Random rand = new Random();
        while(true){

            int index = rand.nextInt(NUMBER_OF_CC);
            String ccNbr = _creditCards.get(index);
            int value = rand.nextInt(10000);
            if(value < 33){
                publishAuth(ccNbr, "FAILED", 3, "FRAUD");
            } else if(value < 100){
                publishAuth(ccNbr, "FAILED", 2, "E");
            } else if(value < 1000){
                publishAuth(ccNbr, "FAILED", 1, "e");
            } else {
                publishAuth(ccNbr, "OK", 1, ".");
            }
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }

    private void terminate(){
        _producer.flush();
    }

    public static void main(String[] args) {
        System.out.println("*** Starting CC Authorization Producer ***");

        AuthorizationProducer producer = new AuthorizationProducer();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("### Stopping CC Authorization Producer ###");
            producer.terminate();
        }));
        
        try{
            producer.produce();
        } catch(IOException ex){
            ex.printStackTrace();
            return;
        } catch(InterruptedException e){
            e.printStackTrace();
            return;
        }
    }
}
