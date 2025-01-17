package kafka;

import model.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import repositories.CustomerOrderRepository;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Consumer {
    static String consumerGroupId = "customerOrderConsumers2";

    static List<KafkaConsumer<ObjectId, String>> consumerGroup;

    static String topicName;

    static CustomerOrderRepository customerOrderRepository = new CustomerOrderRepository();

    private static void initConsumerGroup() {
        consumerGroup = new ArrayList<>();
        Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ObjectIdDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
       // consumerConfig.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192, kafka2:9292, kafka3:9392");

        for (int i = 0; i < 2; i++) {
            KafkaConsumer<ObjectId, String> consumer = new KafkaConsumer<>(consumerConfig);
            consumer.subscribe(List.of(topicName));
            consumerGroup.add(consumer);
        }
    }

    private static void consume(KafkaConsumer<ObjectId, String> consumer) {
        initConsumerGroup();
        try {
            consumer.poll(0);
            Set<TopicPartition> consumerAssigment = consumer.assignment();
            System.out.println(consumer.groupMetadata().memberId() + " " + consumerAssigment);
//            consumer.seekToBeginning(consumerAssigment);

            Duration timeout = Duration.of(100, ChronoUnit.MILLIS);
            MessageFormat formattter = new MessageFormat("Konsument {5}, Temat {0}, partycja {1}, offset {2, number, integer}, klucz {3}, wartość " +
                    "{4}");
            while (true) {
                ConsumerRecords<ObjectId, String> records = consumer.poll(timeout);
                for (ConsumerRecord<ObjectId, String> record : records) {
                    CustomerOrder customerOrder = mapStringToCustomerOrder(record.value());
                    customerOrderRepository.addCustomerOrder(customerOrder);
                    String result = formattter.format(new Object[]{
                            record.topic(),
                            record.partition(),
                            record.offset(),
                            record.key(),
                            record.value(),
                            consumer.groupMetadata().memberId()
                    });
                    System.out.println(result);
                    consumer.commitSync();
                }
            }

        } catch (WakeupException e) {
            System.out.println("Job finished");
        }
    }

    public static void consumeTopicsByGroup(String name) throws InterruptedException {
        topicName = name;
        initConsumerGroup();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (KafkaConsumer<ObjectId, String> consumer : consumerGroup) {
            executorService.execute(() -> consume(consumer));
        }
        Thread.sleep(60000);
        for (KafkaConsumer<ObjectId, String> consumer : consumerGroup) {
            consumer.wakeup();
        }
        executorService.shutdown();
    }

    public static CustomerOrder mapStringToCustomerOrder(String json) {
        JSONObject jsonObject = new JSONObject(json);

        ObjectId id = new ObjectId(jsonObject.getString("orderId"));
        List<Product> products = mapJSONArrayToProducts(jsonObject.getJSONArray("products"));
        Client client = mapJSONObjectToClient(jsonObject.getJSONObject("client"), jsonObject.getString("client_type"));
        double orderPrice = jsonObject.getDouble("orderprice");
        LocalDateTime orderDate = LocalDateTime.ofEpochSecond(jsonObject.getLong("orderDate"), 0, ZoneOffset.UTC);

        return new CustomerOrder(id, client, products, orderDate, orderPrice);
    }

    private static List<Product> mapJSONArrayToProducts(JSONArray jsonArray) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject productJson = jsonArray.getJSONObject(i);
            Product product = new Product(
                    new ObjectId(productJson.getString("_id")),
                    productJson.getString("name"),
                    productJson.getDouble("basePrice"),
                    productJson.getDouble("weight"),
                    productJson.getInt("quantity"),
                    productJson.getString("description")
            );
            products.add(product);
        }
        return products;
    }

    private static Client mapJSONObjectToClient(JSONObject jsonObject, String clientType) {
        return new Client(
                new ObjectId(jsonObject.getString("_id")),
                jsonObject.getString("firstName"),
                jsonObject.getString("lastName"),
                jsonObject.getString("phoneNumber"),
                parseClientType(clientType)
        );
    }

    private static ClientType parseClientType(String type) {
        return switch (type.toLowerCase()) {
            case "gold" -> new ClientTypeGold();
            case "silver" -> new ClientTypeSilver();
            default -> new ClientTypeDefault();
        };
    }
}
