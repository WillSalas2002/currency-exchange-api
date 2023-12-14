package currency_exchange_api.dao;

import currency_exchange_api.model.Currency;

import java.util.List;

public interface CurrencyDAO {
    List<Currency> getCurrencies();
}
