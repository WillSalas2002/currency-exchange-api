package currency.exchange.api.repository;

import currency.exchange.api.exception.CurrencyException;
import currency.exchange.api.model.Currency;
import currency.exchange.api.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCurrencyRepository implements CurrencyRepository {
    private final static String FIND_ALL = "SELECT * FROM currencies";
    private final static String FIND_BY_CODE = "SELECT * FROM currencies WHERE code = ?";
    private final static String FIND_BY_ID = "SELECT * FROM currencies WHERE id = ?";
    private final static String SQL_SAVE_CURRENCY = "INSERT INTO currencies (code, full_name, sign) VALUES (?,?,?)";
    private final static String SQL_DELETE_CURRENCY = "DELETE FROM currencies WHERE id = ?";

    @Override
    public List<Currency> findAll() {
        List<Currency> currencyList = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                currencyList.add(toCurrency(resultSet));
            }
        } catch (SQLException e) {
            throw new CurrencyException("Database error");
        }
        return currencyList;
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODE)) {
            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Currency currency = toCurrency(resultSet);
                return Optional.of(currency);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new CurrencyException("Currency with the specified code not found in db");
        }
    }

    @Override
    public void save(Currency currency) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SAVE_CURRENCY)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            CurrencyException exception = new CurrencyException("Could not save the currency due to db error");
            if (e.getErrorCode() == 19) {
                exception.setMessage("specified currency already exists in db");
                exception.setCode(19);
                throw exception;
            }
            throw exception;
        }
    }

    @Override
    public Currency findById(int id) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return toCurrency(resultSet);
        } catch (SQLException e) {
            throw new CurrencyException("Currency not found");
        }
    }

    @Override
    public void delete(Currency currency) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_CURRENCY)) {
            preparedStatement.setInt(1, currency.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new CurrencyException("specified currency is already absent");
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
