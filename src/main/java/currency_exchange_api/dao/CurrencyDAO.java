package currency_exchange_api.dao;

import currency_exchange_api.model.Currency;
import currency_exchange_api.model.ExchangeRate;

import java.util.List;

public interface CurrencyDAO {

    List<Currency> getCurrencies();
    Currency getCurrencyByCode(String code);
    List<ExchangeRate> getExchangeRates();
    ExchangeRate getExchangeRate(String code1, String code2);
    void saveCurrency(String name, String code, String sign);
    void saveExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate);
    void updateExchangeRate(int id, double rate);
}
