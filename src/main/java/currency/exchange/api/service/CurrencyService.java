package currency.exchange.api.service;

import currency.exchange.api.repository.CurrencyRepository;
import currency.exchange.api.model.Currency;
import currency.exchange.api.repository.JdbcCurrencyRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private final CurrencyRepository currencyRepository = new JdbcCurrencyRepository();

    public List<Currency> findAll() throws SQLException {
        return currencyRepository.findAll();
    }

    public Optional<Currency> findByCode(String code) throws SQLException {
        return currencyRepository.findByCode(code);
    }

    public void save(Currency currency) throws SQLException {
        currencyRepository.save(currency);
    }
}
