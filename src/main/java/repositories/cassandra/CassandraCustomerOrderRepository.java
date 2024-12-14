package repositories.cassandra;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import model.Client;
import model.CustomerOrder;
import model.Product;
import repositories.AbstractCassandraRepository;
import repositories.CustomerOrderRepository;
import repositories.ProductRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

public class CassandraCustomerOrderRepository extends AbstractCassandraRepository implements CustomerOrderRepository {
    private final CqlSession session;
    private final ProductRepository productRepository;

    public CassandraCustomerOrderRepository() {
        initSession();
        productRepository = new CassandraProductRepository();
        this.session = getSession();

        session.execute(SchemaBuilder.createTable(CqlIdentifier.fromCql("orders_by_client"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("client_id"), DataTypes.UUID)
                .withClusteringColumn(CqlIdentifier.fromCql("order_date"), DataTypes.TIMESTAMP)
                .withColumn(CqlIdentifier.fromCql("customer_order_id"), DataTypes.UUID)
                .withColumn(CqlIdentifier.fromCql("order_price"), DataTypes.DOUBLE)
                .withColumn(CqlIdentifier.fromCql("product_id"), DataTypes.UUID)
                .build());
        session.execute(SchemaBuilder.createTable(CqlIdentifier.fromCql("orders_by_product"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("product_id"), DataTypes.UUID)
                .withClusteringColumn(CqlIdentifier.fromCql("order_date"), DataTypes.TIMESTAMP)
                .withColumn(CqlIdentifier.fromCql("customer_order_id"), DataTypes.UUID)
                .withColumn(CqlIdentifier.fromCql("order_price"), DataTypes.DOUBLE)
                .withColumn(CqlIdentifier.fromCql("client_id"), DataTypes.UUID)
                .build());
    }

    @Override
    @StatementAttributes(consistencyLevel = "QUORUM")
    public boolean addCustomerOrder(Client client, List<Product> products) {
        if (products.isEmpty()) {
            throw new RuntimeException("Products in customer order cannot be empty.");
        }
        try {
            List<Product> availableProducts = new ArrayList<>();

            for (Product product : products) {
                Product productFromDb = productRepository.getById(product.getProductId());
                if (productFromDb != null && productFromDb.isAvailable() && productFromDb.getQuantity() > 0) {
                    boolean updated = session.execute(QueryBuilder.update("products")
                            .setColumn("quantity", literal(productFromDb.getQuantity() - 1))
                            .whereColumn("product_id").isEqualTo(literal(product.getProductId()))
                            .ifColumn("quantity").isEqualTo(literal(productFromDb.getQuantity()))
                            .build()).wasApplied();

                    if (updated) {
                        if (productFromDb.getQuantity() == 1) {
                            productFromDb.setQuantity(0);
                            productFromDb.setAvailable(false);
                            productRepository.updateProduct(productFromDb);
                        }
                        availableProducts.add(product);
                    } else {
                        System.out.println("Product " + product.getProductId() + " is unavailable and will not be ordered.");
                    }
                } else {
                    System.out.println("Product " + product.getProductId() + " is unavailable and will not be ordered.");
                }
            }

            if (availableProducts.isEmpty()) {
                throw new RuntimeException("No products are available for ordering.");
            }

            UUID orderId = UUID.randomUUID();

            for (Product product : availableProducts) {
                Instant now = Instant.now();
                double orderPrice = product.getBasePrice() * (1 - client.getDiscount());

                session.execute(QueryBuilder.insertInto("orders_by_client")
                        .value("client_id", literal(client.getClientId()))
                        .value("order_date", literal(now))
                        .value("customer_order_id", literal(orderId))
                        .value("order_price", literal(orderPrice))
                        .value("product_id", literal(product.getProductId()))
                        .build());

                session.execute(QueryBuilder.insertInto("orders_by_product")
                        .value("product_id", literal(product.getProductId()))
                        .value("order_date", literal(now))
                        .value("customer_order_id", literal(orderId))
                        .value("order_price", literal(orderPrice))
                        .value("client_id", literal(client.getClientId()))
                        .build());
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @StatementAttributes(consistencyLevel = "QUORUM")
    public boolean addCustomerOrder(Client client, Product product) {
        return addCustomerOrder(client, List.of(product));
    }

    @Override
    public List<CustomerOrder> getAllClientOrders(UUID clientId) {
        Select select = QueryBuilder.selectFrom("orders_by_client")
                .all()
                .where(Relation.column(CqlIdentifier.fromCql("client_id")).isEqualTo(literal(clientId)));
        ResultSet resultSet = session.execute(select.build());

        return resultSet.all().stream()
                .map(this::mapRowToCustomerOrder)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerOrder> getAllProductOrders(UUID productId) {
        Select select = QueryBuilder.selectFrom("orders_by_product")
                .all()
                .where(Relation.column(CqlIdentifier.fromCql("product_id")).isEqualTo(literal(productId)));
        ResultSet resultSet = session.execute(select.build());
        return resultSet.all().stream()
                .map(this::mapRowToCustomerOrder)
                .collect(Collectors.toList());
    }

    private CustomerOrder mapRowToCustomerOrder(Row row) {
        UUID customerOrderId = row.getUuid("customer_order_id");
        UUID clientId = row.getUuid("client_id");
        UUID productId = row.getUuid("product_id");
        double orderPrice = row.getDouble("order_price");
        Instant orderDate = row.getInstant("order_date");

        return new CustomerOrder(customerOrderId, clientId, productId, orderDate, orderPrice);
    }
}
