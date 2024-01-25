package currency.exchange.api.service;

import currency.exchange.api.dto.ExchangeRateDTO;
import currency.exchange.api.repository.CurrencyRepository;
import currency.exchange.api.repository.ExchangeRateRepository;
import currency.exchange.api.exception.MissingCurrencyException;
import currency.exchange.api.model.ExchangeRate;
import currency.exchange.api.repository.JdbcCurrencyRepository;
import currency.exchange.api.repository.JdbcExchangeRateRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateRepository exchangeRateRepository = new JdbcExchangeRateRepository();
    private final CurrencyRepository currencyRepository = new JdbcCurrencyRepository();

    public List<ExchangeRate> findAll() throws SQLException {
        List<ExchangeRate> exchangeRateList = new ArrayList<>();
        List<ExchangeRateDTO> list = exchangeRateRepository.findAll();
        for (ExchangeRateDTO item : list) {
            exchangeRateList.add(toExchangeRate(item));
        }
        return exchangeRateList;
    }

    public ExchangeRate findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        int baseCurrencyId = currencyRepository.findByCode(baseCurrencyCode).getId();
        int targetCurrencyId = currencyRepository.findByCode(targetCurrencyCode).getId();
        ExchangeRateDTO exchangeRateDTO = exchangeRateRepository.findByCurrencyCodes(baseCurrencyId, targetCurrencyId);
        return toExchangeRate(exchangeRateDTO);
    }

    public void save(ExchangeRate exchangeRate) throws SQLException, MissingCurrencyException {
        exchangeRateRepository.save(exchangeRate);
    }

    public void update(int id, BigDecimal rate) throws SQLException {
        exchangeRateRepository.update(id, rate);
    }

    private ExchangeRate toExchangeRate(ExchangeRateDTO exchangeRateDTO) throws SQLException {
        return new ExchangeRate(
                exchangeRateDTO.getId(),
                currencyRepository.findById(exchangeRateDTO.getBaseCurrencyId()),
                currencyRepository.findById(exchangeRateDTO.getBaseCurrencyId()),
                exchangeRateDTO.getRate()
        );
    }
}