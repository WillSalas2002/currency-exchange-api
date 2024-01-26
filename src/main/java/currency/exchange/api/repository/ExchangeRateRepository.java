package currency.exchange.api.repository;

import currency.exchange.api.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository {

    List<ExchangeRate> findAll() throws SQLException;

    void save(ExchangeRate entity) throws SQLException;

    Optional<ExchangeRate> findByCurrencyCodes(int baseId, int targetId) throws SQLException;

    void update(int id, BigDecimal rate) throws SQLException;
}
