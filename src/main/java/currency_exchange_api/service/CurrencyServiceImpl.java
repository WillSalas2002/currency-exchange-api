package currency_exchange_api.service;

import currency_exchange_api.dao.CurrencyDAO;
import currency_exchange_api.model.Currency;
import currency_exchange_api.model.ExchangeRate;

import java.util.List;

public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyDAO currencyDAO;

    public CurrencyServiceImpl(CurrencyDAO currencyDAO) {
        this.currencyDAO = currencyDAO;
    }
    @Override
    public List<Currency> getCurrencies() {
        return currencyDAO.getCurrencies();
    }
    @Override
    public Currency getCurrencyByCode(String code) {
        return currencyDAO.getCurrencyByCode(code);
    }

    @Override
    public List<ExchangeRate> getExchangeRates() {
        return currencyDAO.getExchangeRates();
    }

    @Override
    public ExchangeRate getExchangeRate(String code1, String code2) {
        return currencyDAO.getExchangeRate(code1, code2);
    }
}
