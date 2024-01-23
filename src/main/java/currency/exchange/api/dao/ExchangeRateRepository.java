package currency.exchange.api.dao;

import currency.exchange.api.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface ExchangeRateRepository extends CRUDRepository<ExchangeRate> {

    ExchangeRate findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) throws SQLException;

    void update(int id, BigDecimal rate) throws SQLException;
}
