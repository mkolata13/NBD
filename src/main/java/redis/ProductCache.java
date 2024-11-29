package redis;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import model.Product;
import org.bson.types.ObjectId;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;
import java.util.stream.Collectors;

public class ProductCache {
    JedisPooled pool;
    Jsonb jsonb = JsonbBuilder.create();
    private static final String hashPrefix = "product:";

    public ProductCache() {
        pool = new RedisConnector().getJedisPool();
    }

    public void addProduct(Product product) {
        String key = hashPrefix + product.getEntityId();
        String productJson = jsonb.toJson(product);

        pool.jsonSet(key, productJson);
        pool.expire(key, 60);
    }

    public Product getProduct(ObjectId id) {
        String key = hashPrefix + id;
        String productJson = jsonb.toJson(pool.jsonGet(key));

        if (productJson != null) {
            return jsonb.fromJson(productJson, Product.class);
        } else {
            return null;
        }
    }

    public void deleteProduct(ObjectId id) {
        pool.del(hashPrefix + id);
    }

    public void decrementQuantityOfProduct(ObjectId id) {
        String key = hashPrefix + id;
        if (pool.exists(key)) {
            String jsonProduct = jsonb.toJson(pool.jsonGet(key));
            Product product = jsonb.fromJson(jsonProduct, Product.class);
            product.setQuantity(product.getQuantity() - 1);
            String updatedProduct = jsonb.toJson(product);
            pool.jsonSet(key, updatedProduct);
        } else {
            throw new RuntimeException("Error while decrementing product.");
        }
    }

    public void setProductUnAvailability(ObjectId id) {
        String key = hashPrefix + id;
        if (pool.exists(key)) {
            String jsonProduct = jsonb.toJson(pool.jsonGet(key));
            Product product = jsonb.fromJson(jsonProduct, Product.class);
            product.setAvailable(false);
            String updatedProduct = jsonb.toJson(product);
            pool.jsonSet(key, updatedProduct);
        } else {
            throw new RuntimeException("Error while setting unavailability of product.");
        }
    }

    public List<Product> getAllProducts() {
        Set<String> productKeys = pool.keys(hashPrefix + '*');

        if (!productKeys.isEmpty()) {
            List<String> productJsonList = new ArrayList<>();
            for (String key : productKeys) {
                productJsonList.add(jsonb.toJson(pool.jsonGet(key)));
            }
            return productJsonList.stream()
                    .map(json -> jsonb.fromJson(json, Product.class))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    public void clearCache() throws JedisException {
        pool.flushAll();
    }

    public void closeCache() {
        pool.flushAll();
        pool.close();
    }
}
