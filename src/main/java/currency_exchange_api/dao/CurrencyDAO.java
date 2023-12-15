package currency_exchange_api.dao;

import currency_exchange_api.model.Currency;
import currency_exchange_api.model.ExchangeRate;

import java.sql.SQLException;
import java.util.List;

public interface CurrencyDAO {

    List<Currency> getCurrencies() throws SQLException;
    Currency getCurrencyByCode(String code) throws SQLException;
    List<ExchangeRate> getExchangeRates() throws SQLException;
    ExchangeRate getExchangeRate(Currency baseCurrency, Currency targetCurrency) throws SQLException;
    void saveCurrency(String name, String code, String sign) throws SQLException;
    void saveExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) throws SQLException;
    void updateExchangeRate(int id, double rate) throws SQLException;
}
