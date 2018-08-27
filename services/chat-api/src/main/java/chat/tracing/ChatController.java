package chat.tracing;

import org.springframework.web.bind.annotation.*;

import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaConsumer;
import io.opentracing.contrib.kafka.TracingKafkaProducer;
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
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@CrossOrigin(maxAge = 3600)
@RestController
public class ChatController {

  @RequestMapping(value = "/message", method = RequestMethod.GET)
  public @ResponseBody List<Message> index(@RequestParam(value = "room", defaultValue = "lobby") String room) throws UnknownHostException {
    List<Message> messages = Message.getMessages(room);
    System.out.println(messages.toString());
    return messages;
  }

  @RequestMapping(value = "/message",  consumes = {"application/json"}, produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST)
  public ResponseEntity<Message> postMessage(@RequestBody Message msg) throws UnknownHostException {
    msg.create();
    return new ResponseEntity<Message>(msg, HttpStatus.OK);
  }
}