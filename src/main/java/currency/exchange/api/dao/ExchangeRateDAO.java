package currency.exchange.api.dao;

import currency.exchange.api.exception.MissingCurrencyException;
import currency.exchange.api.model.ExchangeRate;
import currency.exchange.api.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDAO implements ExchangeRateRepository {

    private final CurrencyRepository currencyRepository = new CurrencyDAO();

    @Override
    public List<ExchangeRate> findAll() throws SQLException {

        String sql = "SELECT * FROM exchange_rates";
        List<ExchangeRate> exchangeRateList = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                exchangeRateList.add(toExchangeRate(resultSet));
            }
            return exchangeRateList;
        }
    }

    @Override
    public ExchangeRate findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {

        int baseId = currencyRepository.findByCode(baseCurrencyCode).getId();
        int targetId = currencyRepository.findByCode(targetCurrencyCode).getId();

        String sql = "SELECT * FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, baseId);
            preparedStatement.setInt(2, targetId);
            ResultSet resultSet = preparedStatement.executeQuery();

            ExchangeRate exchangeRate = toExchangeRate(resultSet);
            if (exchangeRate.getId() == 0) {
                throw new MissingCurrencyException("exchange rate for this pair of currencies is absent in database");
            }
            return exchangeRate;
        }
    }

    @Override
    public void save(ExchangeRate exchangeRate) throws SQLException, MissingCurrencyException {

        String sql = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

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

        String sql = "UPDATE exchange_rates SET rate = ? WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setBigDecimal(1, rate);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }

    private ExchangeRate toExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getInt("id"),
                currencyRepository.findById(resultSet.getInt("base_currency_id")),
                currencyRepository.findById(resultSet.getInt("target_currency_id")),
                resultSet.getBigDecimal("rate")
        );
    }
}
