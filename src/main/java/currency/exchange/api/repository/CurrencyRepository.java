package currency.exchange.api.repository;

import currency.exchange.api.model.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository {

    List<Currency> findAll();

    void save(Currency entity);

    Optional<Currency> findByCode(String code);

    Currency findById(int id);

    void delete(Currency currency);
}
