package currency.exchange.api.service;

import currency.exchange.api.dao.CurrencyRepository;
import currency.exchange.api.model.Currency;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public List<Currency> findAll() throws SQLException {
        return currencyRepository.findAll();
    }

    public Currency findByCode(String code) throws SQLException {
        return currencyRepository.findByCode(code);
    }

    public void save(Currency currency) throws SQLException {
        currencyRepository.save(currency);
    }
}
