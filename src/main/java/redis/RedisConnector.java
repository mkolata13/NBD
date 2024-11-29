package redis;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.*;

import java.io.InputStream;
import java.util.Properties;

@Getter
@Slf4j
public class RedisConnector {
    private JedisPooled jedisPool;

    private void initConnection() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("redis-config.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            JedisClientConfig jedisClientConfig = DefaultJedisClientConfig.builder().build();
            jedisPool = new JedisPooled(new HostAndPort(
                    properties.getProperty("redis.host"),
                    Integer.parseInt(properties.getProperty("redis.port"))),
                    jedisClientConfig);

        } catch (Exception e) {
            log.error("e: ", e);
        }
    }

    public RedisConnector() {
        initConnection();
    }
}
