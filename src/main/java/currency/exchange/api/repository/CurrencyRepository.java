package currency.exchange.api.repository;

import currency.exchange.api.model.Currency;

import java.sql.SQLException;
import java.util.List;

public interface CurrencyRepository {

    List<Currency> findAll() throws SQLException;

    void save(Currency entity) throws SQLException;

    Currency findByCode(String code) throws SQLException;

    Currency findById(int id) throws SQLException;
}
