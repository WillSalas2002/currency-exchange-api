package currency.exchange.api.repository;

import currency.exchange.api.model.Currency;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CurrencyRepository {

    List<Currency> findAll() throws SQLException;

    void save(Currency entity) throws SQLException;

    Optional<Currency> findByCode(String code) throws SQLException;

    Currency findById(int id) throws SQLException;
}
