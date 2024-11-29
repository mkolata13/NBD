package redis;

import model.Product;
import org.bson.types.ObjectId;
import org.openjdk.jmh.annotations.*;
import repositories.ProductRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Fork(value = 1, warmups = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class BenchmarkRunner {
    private static final int NUM_OF_PRODUCTS = 1000;
    private final ProductRepository productRepository = new ProductRepository();

    private final List<ObjectId> ids = new ArrayList<>();
    private boolean cacheClosed = false;

    @Setup
    public void setup() {
        for (int i = 0; i < NUM_OF_PRODUCTS; i++) {
            Product product = new Product("benchamrk test " + i, 10, 10, 20, "a");
            productRepository.addProduct(product);
            ids.add(product.getEntityId());
        }
    }

    @Benchmark
    public void readFromCache() {
        for (ObjectId id : ids) {
            productRepository.getById(id);
        }
    }

    @Benchmark
    public void readFromMongoDB() {
        if (!cacheClosed) {
            productRepository.closeCache();
            cacheClosed = true;
        }
        for (ObjectId id : ids) {
            productRepository.getById(id);
        }
    }

    @TearDown
    public void cleanup() {
        productRepository.dropCollection();
        productRepository.closeCache();
    }

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }
}
