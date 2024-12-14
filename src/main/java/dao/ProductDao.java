package dao;

import com.datastax.oss.driver.api.mapper.annotations.*;
import model.Product;

import java.util.UUID;

@Dao
public interface ProductDao {
    @StatementAttributes(consistencyLevel = "QUORUM")
    @Insert(ifNotExists = true)
    boolean create(Product product);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @Select
    Product getById(UUID id);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @Update
    void update(Product product);

    @Delete(ifExists = true, entityClass = Product.class)
    boolean delete(Product product);
}
