package currency_exchange_api.service;

import currency_exchange_api.model.Currency;
import currency_exchange_api.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface CurrencyService {

    List<Currency> getCurrencies() throws SQLException;
    Currency getCurrencyByCode(String code) throws SQLException;
    List<ExchangeRate> getExchangeRates() throws SQLException;
    ExchangeRate getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException;
    void saveCurrency(String name, String code, String sign) throws SQLException;
    void saveExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException;
    void updateExchangeRate(int id, BigDecimal rate) throws SQLException;
}
