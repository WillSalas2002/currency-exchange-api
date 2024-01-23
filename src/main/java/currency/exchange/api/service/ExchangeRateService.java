package currency.exchange.api.service;

import currency.exchange.api.dao.ExchangeRateRepository;
import currency.exchange.api.exception.MissingCurrencyException;
import currency.exchange.api.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public List<ExchangeRate> findAll() throws SQLException {
        return exchangeRateRepository.findAll();
    }

    public ExchangeRate findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return exchangeRateRepository.findByCurrencyCodes(baseCurrencyCode, targetCurrencyCode);
    }

    public void save(ExchangeRate exchangeRate) throws SQLException, MissingCurrencyException {
        exchangeRateRepository.save(exchangeRate);
    }

    public void update(int id, BigDecimal rate) throws SQLException {
        exchangeRateRepository.update(id, rate);
    }
}