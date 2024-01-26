package currency.exchange.api.repository;

import currency.exchange.api.exception.MissingCurrencyException;
import currency.exchange.api.model.Currency;
import currency.exchange.api.model.ExchangeRate;
import currency.exchange.api.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcExchangeRateRepository implements ExchangeRateRepository {
    private final static String FIND_ALL = "SELECT * FROM exchange_rates";
    private final static String FIND_BY_CURRENCY_CODES = "SELECT * FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?";
    private final static String SQL_INSERT_EXCHANGE_RATE = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";
    private final static String SQL_UPDATE_EXCHANGE_RATE = "UPDATE exchange_rates SET rate = ? WHERE id = ?";

    @Override
    public List<ExchangeRate> findAll() throws SQLException {
        List<ExchangeRate> exchangeRateList = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                ExchangeRate exchangeRate = toExchangeRate(resultSet);
                exchangeRateList.add(exchangeRate);
            }
            return exchangeRateList;
        }
    }

    @Override
    public Optional<ExchangeRate> findByCurrencyCodes(int baseId, int targetId) throws SQLException {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CURRENCY_CODES)) {
            preparedStatement.setInt(1, baseId);
            preparedStatement.setInt(2, targetId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                ExchangeRate exchangeRate = toExchangeRate(resultSet);
                return Optional.of(exchangeRate);
            } else {
                return Optional.empty();
            }
        }
    }

    @Override
    public void save(ExchangeRate exchangeRate) throws SQLException, MissingCurrencyException {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_EXCHANGE_RATE)) {
            preparedStatement.setInt(1, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setInt(2, exchangeRate.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());
            preparedStatement.executeUpdate();
        } catch (MissingCurrencyException e) {
            throw new MissingCurrencyException("Both or one of the currencies you specified is absent in the database.");
        }
    }

    @Override
    public void update(int id, BigDecimal rate) throws SQLException {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_EXCHANGE_RATE)) {
            preparedStatement.setBigDecimal(1, rate);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }

    private ExchangeRate toExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getInt("id"),
                new Currency(resultSet.getInt("base_currency_id")),
                new Currency(resultSet.getInt("target_currency_id")),
                resultSet.getBigDecimal("rate")
        );
    }
}
