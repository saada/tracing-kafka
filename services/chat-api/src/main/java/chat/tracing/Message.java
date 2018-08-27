package chat.tracing;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.opentracing.contrib.redis.lettuce.TracingStatefulRedisConnection;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaConsumer;
import io.opentracing.contrib.kafka.TracingKafkaProducer;
import io.opentracing.util.GlobalTracer;

public class Message {
  private String event;
  private String id;
  private String author;
  private String message;
  private String room;
  private String date;

  Message(String id, String author, String message, String room, String date) {
    this.id = id;
    this.author = author;
    this.message = message;
    this.room = room;
    this.date = date;
  }

  public void init() {
    this.id = UUID.randomUUID().toString();
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
    df.setTimeZone(tz);
    this.date = df.format(new Date());
  }

  public String getId() {
    return this.id;
  }

  public String getAuthor() {
    return this.author;
  }

  public String getRoom() {
    return this.room;
  }

  public String getMessage() {
    return this.message;
  }

  public String getDate() {
    return this.date;
  }

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

  public static List<Message> getMessages(String room) throws UnknownHostException {
    // redis client
    RedisClient client = RedisClient.create("redis://localhost");
    StatefulRedisConnection<String, String> connection = new TracingStatefulRedisConnection<>(client.connect(), GlobalTracer.get(), false);
    RedisCommands<String, String> syncCommands = connection.sync();
    List<String> ids = syncCommands.zrange(room, 0, -1);
    List<Message> messages = new ArrayList();
    ids.forEach(id -> {
      String jsonString = syncCommands.get("message:" + id);
      JsonParser jsonParser = new JsonParser();
      JsonObject json = (JsonObject)jsonParser.parse(jsonString);
      messages.add(new Message(
              json.get("id").getAsString(),
              json.get("author").getAsString(),
              json.get("message").getAsString(),
              json.get("room").getAsString(),
              json.get("date").getAsString()));
    });
    return messages;
  }

  public void create() {
    this.init();
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    this.event = "create";
    String json;
    try {
      json = ow.writeValueAsString(this);
      this.sendKafkaMessage(json);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  public void sendKafkaMessage(String message) {
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "chat-api");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    KafkaProducer<Integer, String> producer = new KafkaProducer<>(props);
    TracingKafkaProducer<Integer, String> tracingProducer = new TracingKafkaProducer<>(producer,
    GlobalTracer.get());
    ProducerRecord<Integer, String> record = new ProducerRecord<>("message", message);
    tracingProducer.send(record);
    tracingProducer.close();
  }
}