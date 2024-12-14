package dao;

import com.datastax.oss.driver.api.mapper.annotations.*;
import model.Client;

import java.util.UUID;

@Dao
public interface ClientDao {
    @StatementAttributes(consistencyLevel = "QUORUM")
    @Insert(ifNotExists = true)
    boolean create(Client client);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @Select
    Client getById(UUID id);

    @StatementAttributes(consistencyLevel = "QUORUM")
    @Update
    void update(Client client);

    @Delete
    boolean delete(Client client);
}
