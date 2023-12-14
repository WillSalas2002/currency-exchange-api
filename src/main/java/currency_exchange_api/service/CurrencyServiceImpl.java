package currency_exchange_api.service;

import currency_exchange_api.dao.CurrencyDAO;
import currency_exchange_api.dao.CurrencyDAOImpl;
import currency_exchange_api.model.Currency;

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
}
