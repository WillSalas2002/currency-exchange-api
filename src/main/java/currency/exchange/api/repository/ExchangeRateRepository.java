package currency.exchange.api.repository;

import currency.exchange.api.dto.ExchangeRateDTO;
import currency.exchange.api.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface ExchangeRateRepository {

    List<ExchangeRateDTO> findAll() throws SQLException;

    void save(ExchangeRate entity) throws SQLException;

    ExchangeRateDTO findByCurrencyCodes(int baseId, int targetId) throws SQLException;

    void update(int id, BigDecimal rate) throws SQLException;
}
