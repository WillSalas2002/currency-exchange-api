package currency.exchange.api.repository;

import currency.exchange.api.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository {

    List<ExchangeRate> findAll();

    void save(ExchangeRate entity);

    Optional<ExchangeRate> findByCurrencyCodes(int baseId, int targetId);

    void update(int id, BigDecimal rate);

    void delete(ExchangeRate exchangeRate);
}
