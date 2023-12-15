package currency_exchange_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import currency_exchange_api.dao.CurrencyDAO;
import currency_exchange_api.dao.CurrencyDAOImpl;
import currency_exchange_api.exception.InvalidParameterException;
import currency_exchange_api.exception.MissingCurrencyException;
import currency_exchange_api.exception.MissingCurrencyPairException;
import currency_exchange_api.model.Currency;
import currency_exchange_api.model.ExchangeRate;
import currency_exchange_api.service.CurrencyService;
import currency_exchange_api.service.CurrencyServiceImpl;
import currency_exchange_api.util.Validation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {

    private final CurrencyDAO currencyDAO = new CurrencyDAOImpl();
    private final CurrencyService currencyService = new CurrencyServiceImpl(currencyDAO);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        try {

            String codeFrom = null;
            String codeTo = null;
            String amount = null;

            Map<String, String[]> parameterMap = req.getParameterMap();
            Set<String> paramNames = parameterMap.keySet();

            for (String paramName : paramNames) {

                String[] values = parameterMap.get(paramName);

                switch (paramName) {
                    case "from" -> codeFrom = values[0].toUpperCase();
                    case "to" -> codeTo = values[0].toUpperCase();
                    case "amount" -> amount = values[0];
                }
            }

            if (!(Validation.isCodeValid(codeTo) && Validation.isCodeValid(codeFrom))) {
                throw new InvalidParameterException("");
            }

            double amountDouble = Double.parseDouble(amount);

            ExchangeRate exchangeRate = getRealExchangeRate(codeFrom, codeTo);

            double rate = exchangeRate.getRate();

            double convertedAmount = rate * amountDouble;

            res.setContentType("application/json");
            ObjectNode objectNode = objectMapper.createObjectNode();

            objectNode.set("baseCurrency", objectMapper.valueToTree(exchangeRate.getBaseCurrency()));
            objectNode.set("targetCurrency", objectMapper.valueToTree(exchangeRate.getTargetCurrency()));
            objectNode.set("rate", objectMapper.valueToTree(rate));
            objectNode.set("amount", objectMapper.valueToTree(amountDouble));
            objectNode.set("convertedAmount", objectMapper.valueToTree(convertedAmount));

            objectMapper.writeValue(res.getOutputStream(), objectNode);

        } catch (InvalidParameterException | NumberFormatException | NullPointerException error) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", error.getMessage()));

        } catch (MissingCurrencyException | MissingCurrencyPairException error) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", error.getMessage()));

        } catch (SQLException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "database is not available."));
        }
    }

    private ExchangeRate getRealExchangeRate(String codeFrom, String codeTo) throws SQLException, MissingCurrencyPairException, MissingCurrencyException {

        Currency baseCurrency = currencyService.getCurrencyByCode(codeFrom);
        Currency targetCurrency = currencyService.getCurrencyByCode(codeTo);

        ExchangeRate exchangeRate;
        try {

            exchangeRate = currencyService.getExchangeRate(baseCurrency, targetCurrency);

        } catch (MissingCurrencyPairException e) {

            exchangeRate = currencyService.getExchangeRate(targetCurrency, baseCurrency);
            double rate = 1 / exchangeRate.getRate();
            exchangeRate.setRate(rate);
        }

        return exchangeRate;
    }
}
