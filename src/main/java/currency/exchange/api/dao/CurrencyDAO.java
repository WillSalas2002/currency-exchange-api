package currency.exchange.api.dao;

import currency.exchange.api.exception.MissingCurrencyException;
import currency.exchange.api.model.Currency;
import currency.exchange.api.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO implements CurrencyRepository {
    @Override
    public List<Currency> findAll() throws SQLException {

        String sql = "SELECT * FROM currencies";
        List<Currency> currencyList = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                currencyList.add(toCurrency(resultSet));
            }
        }
        return currencyList;
    }

    @Override
    public Currency findByCode(String code) throws SQLException {

        String sql = "SELECT * FROM currencies WHERE code = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();

            Currency currency = toCurrency(resultSet);

            if (currency.getId() == 0) {
                throw new MissingCurrencyException("given currency is absent in database");
            }
            return currency;
        }
    }

    @Override
    public void save(Currency currency) throws SQLException {

        String sql = "INSERT INTO currencies (code, full_name, sign) VALUES (?,?,?)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Currency findById(int id) throws SQLException {

        String sql = "SELECT * FROM currencies WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            return toCurrency(resultSet);
        }
    }

    @Override
    public Currency toCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign")
        );
    }
}
