package repositories.cassandra;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import dao.ProductDao;
import mappers.ProductMapper;
import mappers.ProductMapperBuilder;
import model.Product;
import repositories.AbstractCassandraRepository;
import repositories.ProductRepository;

import java.util.UUID;

public class CassandraProductRepository extends AbstractCassandraRepository implements ProductRepository {
    private final ProductDao productDao;

    public CassandraProductRepository() {
        initSession();
        CqlSession session = getSession();

        SimpleStatement createProducts = SchemaBuilder.createTable(CqlIdentifier.fromCql("products"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("product_id"), DataTypes.UUID)
                .withColumn(CqlIdentifier.fromCql("name"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("base_price"), DataTypes.DOUBLE)
                .withColumn(CqlIdentifier.fromCql("weight"), DataTypes.DOUBLE)
                .withColumn(CqlIdentifier.fromCql("quantity"), DataTypes.INT)
                .withColumn(CqlIdentifier.fromCql("description"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("available"), DataTypes.BOOLEAN)
                .build();
        session.execute(createProducts);
        ProductMapper productMapper = new ProductMapperBuilder(session).build();
        this.productDao = productMapper.productMapper();
    }

    @Override
    public boolean addProduct(Product product) {
        return productDao.create(product);
    }

    @Override
    public boolean deleteProduct(Product product) {
        return productDao.delete(product);
    }

    @Override
    public void updateProduct(Product product) {
        if (product != null) {
            productDao.update(product);
        }
    }

    @Override
    public Product getById(UUID id) {
        return productDao.getById(id);
    }
}
