package mappers;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.DaoTable;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import dao.ProductDao;

@Mapper
public interface ProductMapper {
    @DaoFactory
    ProductDao productMapper(@DaoKeyspace String keyspace, @DaoTable String table);

    @DaoFactory
    ProductDao productMapper();
}
