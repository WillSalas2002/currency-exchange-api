package currency.exchange.api.service;

import currency.exchange.api.repository.CurrencyRepository;
import currency.exchange.api.model.Currency;
import currency.exchange.api.repository.JdbcCurrencyRepository;

import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private final CurrencyRepository currencyRepository = new JdbcCurrencyRepository();

    public List<Currency> findAll() {
        return currencyRepository.findAll();
    }

    public Optional<Currency> findByCode(String code) {
        return currencyRepository.findByCode(code);
    }

    public void save(Currency currency) {
        currencyRepository.save(currency);
    }
}
