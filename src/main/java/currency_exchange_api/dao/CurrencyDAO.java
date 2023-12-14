package currency_exchange_api.dao;

import currency_exchange_api.model.Currency;
import currency_exchange_api.model.ExchangeRate;

import java.util.List;

public interface CurrencyDAO {

    List<Currency> getCurrencies();
    Currency getCurrencyByCode(String code);

    List<ExchangeRate> getExchangeRates();
}
