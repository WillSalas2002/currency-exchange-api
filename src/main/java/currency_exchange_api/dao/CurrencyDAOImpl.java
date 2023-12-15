package currency_exchange_api.dao;

import currency_exchange_api.model.Currency;
import currency_exchange_api.model.ExchangeRate;
import currency_exchange_api.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAOImpl implements CurrencyDAO {
    @Override
    public List<Currency> getCurrencies() {

        String sql = "SELECT * FROM currencies";
        return getCurrency(sql);
    }

    @Override
    public Currency getCurrencyByCode(String code) {

        String sql = "SELECT * FROM currencies WHERE code = '" + code + "'";
        return getCurrency(sql).get(0);
    }

    @Override
    public List<ExchangeRate> getExchangeRates() {

        String sql = "SELECT * FROM exchange_rates";
        List<ExchangeRate> exchangeRateList = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                int baseCurrencyId = resultSet.getInt("base_currency");
                int targetCurrencyId = resultSet.getInt("target_currency");
                double rate = resultSet.getDouble("rate");

                Currency baseCurrency = getCurrencyById(baseCurrencyId).get(0);
                Currency targetCurrency = getCurrencyById(targetCurrencyId).get(0);
                exchangeRateList.add(new ExchangeRate(id, baseCurrency, targetCurrency, rate));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return exchangeRateList;
    }

    @Override
    public ExchangeRate getExchangeRate(String code1, String code2) {

        Currency baseCurrency = getCurrencyByCode(code1);
        Currency targetCurrency = getCurrencyByCode(code2);
        int baseId = baseCurrency.getId();
        int targetId = targetCurrency.getId();
        String sql = "SELECT * FROM exchange_rates WHERE base_currency = " + baseId + " AND target_currency = " + targetId;
        ExchangeRate exchangeRate = null;

        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql);) {

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                int baseCurrencyId = resultSet.getInt("base_currency");
                int targetCurrencyId = resultSet.getInt("target_currency");
                double rate = resultSet.getDouble("rate");

                Currency baseCurrency1 = getCurrencyById(baseCurrencyId).get(0);
                Currency targetCurrency1 = getCurrencyById(targetCurrencyId).get(0);
                exchangeRate = new ExchangeRate(id, baseCurrency1, targetCurrency1, rate);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return exchangeRate;
    }

    @Override
    public void saveCurrency(String name, String code, String sign) {

        String sql = "INSERT INTO currencies (code, full_name, sign) VALUES (?,?,?)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, code);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, sign);

            int i = preparedStatement.executeUpdate();
            System.out.println("Data inserted successfully " + i);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) {

        Currency baseCurrency = getCurrencyByCode(baseCurrencyCode);
        Currency targetCurrency = getCurrencyByCode(targetCurrencyCode);

        int baseCurrencyId = baseCurrency.getId();
        int targetCurrencyId = targetCurrency.getId();

        String sql = "INSERT INTO exchange_rates (base_currency, target_currency, rate) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);
            preparedStatement.setDouble(3, rate);

            preparedStatement.executeUpdate();
            System.out.println("Data inserted successfully!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void updateExchangeRate(int id, String rate) {

        double ratedb = Double.parseDouble(rate);

        String sql = "UPDATE exchange_rates SET rate = ? WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);) {

            preparedStatement.setDouble(1, ratedb);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
            System.out.println("Source updated successfully!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Currency> getCurrencyById(int id) {

        String sql = "SELECT * FROM currencies WHERE id = " + id;
        return getCurrency(sql);
    }

    private List<Currency> getCurrency(String sql) {
        List<Currency> currencyList = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql);) {

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                String code = resultSet.getString("code");
                String fullName = resultSet.getString("full_name");
                String sign = resultSet.getString("sign");

                currencyList.add(new Currency(id, code, fullName, sign));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return currencyList;
    }
}
