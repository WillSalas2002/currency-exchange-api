package currency.exchange.api.dao;

import currency.exchange.api.model.Currency;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface CurrencyRepository extends CRUDRepository<Currency> {

    Currency findByCode(String code) throws SQLException;

    Currency toCurrency(ResultSet resultSet) throws SQLException;

    Currency findById(int id) throws SQLException;
}
