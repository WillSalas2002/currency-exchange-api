package currency.exchange.api.dao;

import java.sql.SQLException;
import java.util.List;

public interface CRUDRepository<T> {

    List<T> findAll() throws SQLException;

    void save(T entity) throws SQLException;
}
