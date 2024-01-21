package currency_exchange_api.dao;

import currency_exchange_api.exception.MissingCurrencyException;
import currency_exchange_api.model.Currency;
import currency_exchange_api.model.ExchangeRate;
import currency_exchange_api.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAOImpl implements CurrencyDAO {
    @Override
    public List<Currency> getCurrencies() throws SQLException {

        String sql = "SELECT * FROM currencies";
        List<Currency> currencyList = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                currencyList.add(getCurrency(resultSet));
            }
        }
        return currencyList;
    }

    @Override
    public Currency getCurrencyByCode(String code) throws SQLException {

        String sql = "SELECT * FROM currencies WHERE code = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();

            Currency currency = getCurrency(resultSet);

            if (currency.getId() == 0) {
                throw new MissingCurrencyException("given currency is absent in database");
            }
            return currency;
        }
    }

    @Override
    public List<ExchangeRate> getExchangeRates() throws SQLException {

        String sql = "SELECT * FROM exchange_rates";
        List<ExchangeRate> exchangeRateList = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                exchangeRateList.add(getExchangeRate(resultSet));
            }

            return exchangeRateList;
        }
    }

    @Override
    public ExchangeRate getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {

        int baseId = getCurrencyByCode(baseCurrencyCode).getId();
        int targetId = getCurrencyByCode(targetCurrencyCode).getId();

        String sql = "SELECT * FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, baseId);
            preparedStatement.setInt(2, targetId);
            ResultSet resultSet = preparedStatement.executeQuery();

            ExchangeRate exchangeRate = getExchangeRate(resultSet);
            if (exchangeRate.getId() == 0) {
                throw new MissingCurrencyException("exchange rate for this pair of currencies is absent in database");
            }
            return exchangeRate;
        }
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
    public void saveExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException, MissingCurrencyException {

        String sql = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            Currency baseCurrency = getCurrencyByCode(baseCurrencyCode);
            Currency targetCurrency = getCurrencyByCode(targetCurrencyCode);

            int baseCurrencyId = baseCurrency.getId();
            int targetCurrencyId = targetCurrency.getId();

            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);
            preparedStatement.setBigDecimal(3, rate);

            preparedStatement.executeUpdate();

        } catch (MissingCurrencyException e) {
            throw new MissingCurrencyException("Both or one of the currencies you specified is absent in the database.");
        }

    }

    @Override
    public void updateExchangeRate(int id, BigDecimal rate) throws SQLException {

        String sql = "UPDATE exchange_rates SET rate = ? WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setBigDecimal(1, rate);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();

        }
    }

    private Currency getCurrencyById(int id) throws SQLException {

        String sql = "SELECT * FROM currencies WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            return getCurrency(resultSet);
        }
    }

    private Currency getCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign")
        );
    }

    private ExchangeRate getExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getInt("id"),
                getCurrencyById(resultSet.getInt("base_currency_id")),
                getCurrencyById(resultSet.getInt("target_currency_id")),
                resultSet.getBigDecimal("rate")
        );
    }
}
