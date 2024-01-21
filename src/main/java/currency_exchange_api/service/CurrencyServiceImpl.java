package currency_exchange_api.service;

import currency_exchange_api.dao.CurrencyDAO;
import currency_exchange_api.model.Currency;
import currency_exchange_api.model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyDAO currencyDAO;

    public CurrencyServiceImpl(CurrencyDAO currencyDAO) {
        this.currencyDAO = currencyDAO;
    }
    @Override
    public List<Currency> getCurrencies() throws SQLException {
        return currencyDAO.getCurrencies();
    }
    @Override
    public Currency getCurrencyByCode(String code) throws SQLException {
        return currencyDAO.getCurrencyByCode(code);
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws SQLException {
        return currencyDAO.getExchangeRates();
    }

    @Override
    public ExchangeRate getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return currencyDAO.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
    }

    @Override
    public void saveCurrency(String name, String code, String sign) throws SQLException {
        currencyDAO.saveCurrency(name, code, sign);
    }

    @Override
    public void saveExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException {
        currencyDAO.saveExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
    }

    @Override
    public void updateExchangeRate(int id, BigDecimal rate) throws SQLException {
        currencyDAO.updateExchangeRate(id, rate);
    }
}
