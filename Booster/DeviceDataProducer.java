package Booster;

import java.io.IOException;
//import util.properties packages
import java.util.Properties;

//import simple producer packages
import org.apache.kafka.clients.producer.Producer;

//import KafkaProducer packages
import org.apache.kafka.clients.producer.KafkaProducer;

//import ProducerRecord packages
import org.apache.kafka.clients.producer.ProducerRecord;

public class DeviceDataProducer {
 
	public void sendToKafka(String key, String value) throws org.apache.kafka.common.protocol.types.SchemaException, 
		IOException{
		
		 Properties props = new Properties();
		 props.put("bootstrap.servers", "ec2-54-69-254-10.us-west-2.compute.amazonaws.com:9092");
		 props.put("acks", "all");
		 props.put("retries", 0);
		 props.put("batch.size", 16384);
		 props.put("linger.ms", 1);
		 props.put("buffer.memory", 33554432);
		
		 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		 props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		 
		 Producer<String, String> producer = new KafkaProducer<String, String>(props);
		 System.out.println("Sending to deviceSample topic");
	     producer.send(new ProducerRecord<String, String>("sample", key, value));
	     System.out.println("Done topic");
		 producer.close(); 	  
	}
}