package currency.exchange.api.repository;

import currency.exchange.api.exception.MissingCurrencyException;
import currency.exchange.api.model.Currency;
import currency.exchange.api.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcCurrencyRepository implements CurrencyRepository {
    private final static String FIND_ALL = "SELECT * FROM currencies";
    private final static String FIND_BY_CODE = "SELECT * FROM currencies WHERE code = ?";
    private final static String FIND_BY_ID = "SELECT * FROM currencies WHERE id = ?";
    private final static String SQL_SAVE_CURRENCY = "INSERT INTO currencies (code, full_name, sign) VALUES (?,?,?)";

    @Override
    public List<Currency> findAll() throws SQLException {
        List<Currency> currencyList = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                currencyList.add(toCurrency(resultSet));
            }
        }
        return currencyList;
    }

    @Override
    public Currency findByCode(String code) throws SQLException {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODE)) {
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
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SAVE_CURRENCY)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();
        }
    }

    public Currency findById(int id) throws SQLException {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return toCurrency(resultSet);
        }
    }

    private Currency toCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign")
        );
    }
}
