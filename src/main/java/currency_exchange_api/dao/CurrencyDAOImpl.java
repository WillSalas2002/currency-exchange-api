package currency_exchange_api.dao;

import currency_exchange_api.exception.MissingCurrencyException;
import currency_exchange_api.exception.MissingCurrencyPairException;
import currency_exchange_api.model.Currency;
import currency_exchange_api.model.ExchangeRate;
import currency_exchange_api.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAOImpl implements CurrencyDAO {
    @Override
    public List<Currency> getCurrencies() throws SQLException {

        String sql = "SELECT * FROM currencies";
        return getCurrency(sql);
    }

    @Override
    public Currency getCurrencyByCode(String code) throws SQLException {

        String sql = "SELECT * FROM currencies WHERE code = '" + code + "'";

        return getCurrency(sql).get(0);
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws SQLException {

        String sql = "SELECT * FROM exchange_rates";
        return getExchangeRateList(sql);
    }

    @Override
    public ExchangeRate getExchangeRate(Currency baseCurrency, Currency targetCurrency) throws SQLException {

        int baseId = baseCurrency.getId();
        int targetId = targetCurrency.getId();

        String sql = "SELECT * FROM exchange_rates WHERE base_currency = " + baseId + " AND target_currency = " + targetId;

        return getExchangeRateList(sql).get(0);
    }

    @Override
    public void saveCurrency(String name, String code, String sign) throws SQLException {

        String sql = "INSERT INTO currencies (code, full_name, sign) VALUES (?,?,?)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, code);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, sign);

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void saveExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) throws SQLException, MissingCurrencyException{

        String sql = "INSERT INTO exchange_rates (base_currency, target_currency, rate) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            Currency baseCurrency = getCurrencyByCode(baseCurrencyCode);
            Currency targetCurrency = getCurrencyByCode(targetCurrencyCode);

            int baseCurrencyId = baseCurrency.getId();
            int targetCurrencyId = targetCurrency.getId();

            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);
            preparedStatement.setDouble(3, rate);

            preparedStatement.executeUpdate();

        } catch (MissingCurrencyException e) {
            throw new MissingCurrencyException("Both or one of the currencies you specified is absent in the database.");
        }

    }

    @Override
    public void updateExchangeRate(int id, double rate) throws SQLException {

        String sql = "UPDATE exchange_rates SET rate = ? WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setDouble(1, rate);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();

        }
    }

    private List<Currency> getCurrencyById(int id) throws SQLException {

        String sql = "SELECT * FROM currencies WHERE id = " + id;
        return getCurrency(sql);
    }

    private List<Currency> getCurrency(String sql) throws SQLException, MissingCurrencyPairException {
        List<Currency> currencyList = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.isBeforeFirst()) {

                while (resultSet.next()) {

                    int id = resultSet.getInt("id");
                    String code = resultSet.getString("code");
                    String fullName = resultSet.getString("full_name");
                    String sign = resultSet.getString("sign");

                    currencyList.add(new Currency(id, code, fullName, sign));
                }

            } else {
                throw new MissingCurrencyException("Currency does not exist in the database.");
            }
        }
        return currencyList;
    }

    private List<ExchangeRate> getExchangeRateList(String sql) throws SQLException, MissingCurrencyPairException {

        List<ExchangeRate> exchangeRateList = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.isBeforeFirst()) {

                while (resultSet.next()) {

                    int id = resultSet.getInt("id");
                    int baseCurrencyId = resultSet.getInt("base_currency");
                    int targetCurrencyId = resultSet.getInt("target_currency");
                    double rate = resultSet.getDouble("rate");

                    Currency baseCurrency = getCurrencyById(baseCurrencyId).get(0);
                    Currency targetCurrency = getCurrencyById(targetCurrencyId).get(0);
                    exchangeRateList.add(new ExchangeRate(id, baseCurrency, targetCurrency, rate));
                }

            } else {
                throw new MissingCurrencyPairException("requested currency pair does not exist in the database.");
            }
        }

        return exchangeRateList;
    }
}
