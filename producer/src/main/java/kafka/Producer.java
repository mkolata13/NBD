package kafka;

import model.Client;
import model.CustomerOrder;
import model.Product;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.serialization.StringSerializer;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Producer {
    static KafkaProducer<ObjectId, String> producer;
    static String topicName;

    public Producer() throws ExecutionException, InterruptedException {
        initProducer();
    }

    public KafkaProducer<ObjectId, String> getProducer() {
        return producer;
    }

    public static void initProducer() throws ExecutionException, InterruptedException {
        Properties producerConfig = new Properties();
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ObjectIdSerializer.class.getName());
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerConfig.put(ProducerConfig.CLIENT_ID_CONFIG, "local");
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192, kafka2:9292, kafka3:9392");
        // producerConfig.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "Yjk2NWYwYTE4Yzg0NGE5M2");

        producer = new KafkaProducer<>(producerConfig);
    }

    public static void createTopic(String name) throws InterruptedException {
        topicName = name;
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192, kafka2:9292, kafka3:9392");
        int partitionsNumber = 3;
        short replicationFactor = 3;

        try (Admin admin = Admin.create(properties)) {
            NewTopic newTopic = new NewTopic(topicName, partitionsNumber, replicationFactor);
            CreateTopicsOptions options = new CreateTopicsOptions()
                    .timeoutMs(1000)
                    .validateOnly(false)
                    .retryOnQuotaViolation(true);
            CreateTopicsResult result = admin.createTopics(List.of(newTopic), options);
            KafkaFuture<Void> futureResult = result.values().get(topicName);
            futureResult.get();
        } catch (ExecutionException ee) {
            System.out.println(ee.getCause());
        }
    }

    public static void sendCustomerOrder(CustomerOrder customerOrder) {
        try {
            initProducer();
            JSONObject jsonCustomerOrder = mapCustomerOrderToJSON(customerOrder);
            ProducerRecord<ObjectId, String> record = new ProducerRecord<>(topicName, customerOrder.getEntityId(), jsonCustomerOrder.toString());
            Future<RecordMetadata> sent = producer.send(record);
            RecordMetadata recordMetadata = sent.get();
            System.out.println(recordMetadata);
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e.getCause());
        }
    }

    private static JSONObject mapCustomerOrderToJSON(CustomerOrder customerOrder) {
        JSONObject jsonCustomerOrder = new JSONObject();
        jsonCustomerOrder.put("orderId", customerOrder.getEntityId());
        jsonCustomerOrder.put("orderDate", customerOrder.getOrderDate().toEpochSecond(ZoneOffset.UTC));
        jsonCustomerOrder.put("orderPrice", customerOrder.getOrderPrice());
        jsonCustomerOrder.put("client", mapClientToJSON(customerOrder.getClient()));
        jsonCustomerOrder.put("clientType", customerOrder.getClient().getClientType().getTypeName());
        jsonCustomerOrder.put("products", mapProductsToJSON(customerOrder.getProducts()));
        System.out.println(jsonCustomerOrder);
        return jsonCustomerOrder;
    }

    private static JSONArray mapProductsToJSON(List<Product> products) {
        JSONArray jsonProducts = new JSONArray();
        for (Product product : products) {
            JSONObject jsonProduct = new JSONObject();
            ObjectId objectId = product.getEntityId();
            jsonProduct.put("productId", objectId.toString());
            jsonProduct.put("name", product.getName());
            jsonProduct.put("basePrice", product.getBasePrice());
            jsonProduct.put("weight", product.getWeight());
            jsonProduct.put("quantity", product.getQuantity());
            jsonProduct.put("description", product.getDescription());
            jsonProduct.put("isAvailable", product.isAvailable());
            jsonProducts.put(jsonProduct);
        }
        return jsonProducts;
    }

    private static JSONObject mapClientToJSON(Client client) {
        JSONObject jsonClient = new JSONObject();
        jsonClient.put("clientId", client.getEntityId().toString());
        jsonClient.put("firstName", client.getFirstName());
        jsonClient.put("lastName", client.getLastName());
        jsonClient.put("phoneNumber", client.getPhoneNumber());

        JSONObject jsonClientType = new JSONObject();
        jsonClientType.put("_clazz", client.getClientType().getClass().getSimpleName().toLowerCase());
        jsonClientType.put("discount", client.getClientType().getDiscount());

        jsonClient.put("client_type", jsonClientType);

        return jsonClient;
    }
}
