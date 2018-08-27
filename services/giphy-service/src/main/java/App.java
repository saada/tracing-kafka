import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.Collections;

import java.util.concurrent.ExecutionException;

import com.google.common.collect.ImmutableMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.opentracing.SpanContext;
import io.opentracing.contrib.kafka.TracingKafkaProducer;
import io.opentracing.contrib.kafka.TracingKafkaUtils;
import io.opentracing.contrib.redis.lettuce.TracingStatefulRedisConnection;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.Configuration.SenderConfiguration;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaConsumer;
import io.opentracing.util.GlobalTracer;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {
    private final static String JAEGER_AGENT_HOST = "localhost";
    private final static String GIPHY_API_TOKEN = "DhFQzq6E4uSzDgx6FFmTC0xqV0iFYDFK";

    private static Consumer<Integer, String> createConsumer() throws UnknownHostException {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put("client.id", InetAddress.getLocalHost().getHostName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "giphy-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");

        final Consumer<Integer, String> consumer = new TracingKafkaConsumer<>(new KafkaConsumer<>(props), GlobalTracer.get());
        return consumer;
    }

    public static void initTracer(String service) {
        Tracer tracer = new Configuration(service)
            .withSampler(
                new SamplerConfiguration()
                .withType("const")
                .withParam(1)
            )
            .withReporter(
                new ReporterConfiguration()
                .withSender(
                    new SenderConfiguration()
                    .withAgentHost(JAEGER_AGENT_HOST)
                )
            )
            .getTracer();
        GlobalTracer.register(tracer);
    }

    public static String queryGiphy(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            URL url = new URL("http://api.giphy.com/v1/gifs/search?q="+encodedQuery+"&api_key="+GIPHY_API_TOKEN+"&limit=1&rating=pg");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            JsonParser jsonParser = new JsonParser();
            JsonObject json = (JsonObject)jsonParser.parse(content.toString());
            String giphyUrl = json
                    .getAsJsonArray("data")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("images")
                    .getAsJsonObject("original")
                    .get("url")
                    .getAsString();
            return giphyUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendKafkaMessage(String message) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "giphy-service");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<Integer, String> producer = new KafkaProducer<>(props);
        TracingKafkaProducer<Integer, String> tracingProducer = new TracingKafkaProducer<>(producer,
                GlobalTracer.get());
        ProducerRecord<Integer, String> record = new ProducerRecord<>("message", message);
        tracingProducer.send(record);
        tracingProducer.close();
    }

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        initTracer("giphy-service");

        // kafka consumer
        Consumer<Integer, String> consumer = createConsumer();
        String topic = "message";
        consumer.subscribe(Collections.singletonList(topic));
        ConsumerRecords<Integer, String> consumerRecords;

        while (true) {
            System.out.println("POLLING");
            consumerRecords = consumer.poll(1000);
            System.out.println("DONE POLLING");

            consumerRecords.forEach(record -> {
                SpanContext spanContext = TracingKafkaUtils.extractSpanContext(record.headers(), GlobalTracer.get());
                try (Scope scope = GlobalTracer.get().buildSpan("giphy").asChildOf(spanContext).startActive(true)) {
                    System.out.printf("Consumer Record:(%d, %s, %d, %d)\n", record.key(), record.value(), record.partition(), record.offset());
                    // parse json
                    String jsonString = record.value();
                    JsonParser jsonParser = new JsonParser();
                    JsonObject json = (JsonObject)jsonParser.parse(jsonString);
                    String id = json.get("id").getAsString();
                    String message = json.get("message").getAsString();
                    if (json.get("image") instanceof JsonNull && message.startsWith("/giphy")) {
                        // populate giphy url
                        String query = message.split("/giphy")[1];
                        String gifUrl = queryGiphy(query);
                        json.addProperty("image", gifUrl);
                        // send kafka message
                        sendKafkaMessage(json.toString());
                    }
                }
            });

            consumer.commitAsync();
            System.out.println("DONE");
            Thread.sleep(1000);
        }
    }
}
