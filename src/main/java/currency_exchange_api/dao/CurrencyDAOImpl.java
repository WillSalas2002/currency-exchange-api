package currency_exchange_api.dao;

import currency_exchange_api.model.Currency;
import currency_exchange_api.model.ExchangeRate;
import currency_exchange_api.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAOImpl implements CurrencyDAO {
    @Override
    public List<Currency> getCurrencies() {

        String sql = "SELECT * FROM currencies";
        List<Currency> currencyList = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                String code = resultSet.getString("code");
                String fullName = resultSet.getString("full_name");
                String sign = resultSet.getString("sign");

                currencyList.add(new Currency(id, code, fullName, sign));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currencyList;
    }

    @Override
    public Currency getCurrencyByCode(String code) {

        String sql = "SELECT * FROM currencies WHERE code = '" + code + "'";
        return getCurrency(sql);
    }

    @Override
    public List<ExchangeRate> getExchangeRates() {

        String sql = "SELECT * FROM exchange_rates";
        List<ExchangeRate> exchangeRateList = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection()) {

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                int baseCurrencyId = resultSet.getInt("base_currency");
                int targetCurrencyId = resultSet.getInt("target_currency");
                double rate = resultSet.getDouble("rate");

                Currency baseCurrency = getCurrencyById(baseCurrencyId);
                Currency targetCurrency = getCurrencyById(targetCurrencyId);
                exchangeRateList.add(new ExchangeRate(id, baseCurrency, targetCurrency, rate));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return exchangeRateList;
    }

    private Currency getCurrencyById(int id) {

        String sql = "SELECT * FROM currencies WHERE id = " + id;
        return getCurrency(sql);
    }

    private Currency getCurrency(String sql) {
        Currency currency = null;

        try (Connection connection = DatabaseUtil.getConnection()) {

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                String code = resultSet.getString("code");
                String fullName = resultSet.getString("full_name");
                String sign = resultSet.getString("sign");

                currency = new Currency(id, code, fullName, sign);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return currency;
    }
}
