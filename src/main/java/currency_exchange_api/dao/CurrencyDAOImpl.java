package currency_exchange_api.dao;

import currency_exchange_api.model.Currency;
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

        try (Connection connection = DatabaseUtil.getConnection();
             ) {
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
}
