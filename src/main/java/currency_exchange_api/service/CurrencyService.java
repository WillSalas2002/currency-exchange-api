package currency_exchange_api.service;

import currency_exchange_api.model.Currency;
import java.util.List;

public interface CurrencyService {
    List<Currency> getCurrencies();
}
