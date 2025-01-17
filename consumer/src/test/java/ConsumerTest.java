import kafka.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConsumerTest {
    @Test
    public void consumeTest() throws InterruptedException {
        Consumer.consumeTopicsByGroup("customerorders2");
    }
}