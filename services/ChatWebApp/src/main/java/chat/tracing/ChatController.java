package chat.tracing;

import org.springframework.web.bind.annotation.RestController;

import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaConsumer;
import io.opentracing.util.GlobalTracer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(maxAge = 3600)
@RestController
public class ChatController {
  private static Consumer<Integer, String> createConsumer() throws UnknownHostException {
    final Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put("client.id", InetAddress.getLocalHost().getHostName());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "chat-api");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");

    final Consumer<Integer, String> consumer = new KafkaConsumer<>(props);

    return consumer;
  }

  private static ConsumerRecords<Integer, String> getMessages(String topic) throws UnknownHostException {
    // Decorate KafkaConsumer with TracingKafkaConsumer
    Tracer tracer = GlobalTracer.get();
    TracingKafkaConsumer<Integer, String> consumer = new TracingKafkaConsumer<>(createConsumer(), tracer);

    // print list of topics
    Map<String, List<PartitionInfo>> topics = consumer.listTopics();
    for (String topicName : topics.keySet()) {
      System.out.println("topic: " + topicName);
    }

    // consume messages from passed topic
    consumer.subscribe(Collections.singletonList(topic));
    ConsumerRecords<Integer, String> consumerRecords;
    System.out.println("POLLING");
    consumerRecords = consumer.poll(1000);
    System.out.println("DONE POLLING");
    consumerRecords.forEach(record -> {
      System.out.printf("Consumer Record:(%d, %s, %d, %d)\n", record.key(), record.value(), record.partition(),
          record.offset());
    });

    consumer.commitAsync();
    consumer.close();
    System.out.println("DONE");
    return consumerRecords;
  }

  @RequestMapping(value = "/message", method = RequestMethod.GET)
  public String index(@RequestParam(value = "room", defaultValue = "lobby") String room) throws UnknownHostException {
    String topic = room;
    System.out.println(getMessages(topic).toString());
    return "This is awesome";
  }

  @RequestMapping(value = "/message",  consumes = {"application/json"}, produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST)
  public ResponseEntity<Message> postMessage(@RequestBody Message msg) throws UnknownHostException {
    msg.init();
    System.out.println(msg.toString());
    return new ResponseEntity<Message>(msg, HttpStatus.OK);
  }
}