package currency_exchange_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import currency_exchange_api.dao.CurrencyDAO;
import currency_exchange_api.dao.CurrencyDAOImpl;
import currency_exchange_api.model.ExchangeRate;
import currency_exchange_api.service.CurrencyService;
import currency_exchange_api.service.CurrencyServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {

    private final CurrencyDAO currencyDAO = new CurrencyDAOImpl();
    private final CurrencyService currencyService = new CurrencyServiceImpl(currencyDAO);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Map<String, String[]> parameterMap = req.getParameterMap();

        Set<String> names = parameterMap.keySet();
        String codeFrom = null;
        String codeTo = null;
        String amount = null;

        for (String paramName : names) {

            String[] values = parameterMap.get(paramName);

            switch (paramName) {
                case "from" -> codeFrom = values[0];
                case "to" -> codeTo = values[0];
                case "amount" -> amount = values[0];
            }
        }

        res.setContentType("application/json");

        double amountDouble = Double.parseDouble(amount);
        ExchangeRate exchangeRate = getRealExchangeRate(codeFrom, codeTo);
        double rate = exchangeRate.getRate();

        double convertedAmount = rate * amountDouble;

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.set("baseCurrency", objectMapper.valueToTree(exchangeRate.getBaseCurrency()));
        objectNode.set("targetCurrency", objectMapper.valueToTree(exchangeRate.getTargetCurrency()));
        objectNode.set("rate", objectMapper.valueToTree(rate));
        objectNode.set("amount", objectMapper.valueToTree(amountDouble));
        objectNode.set("convertedAmount", objectMapper.valueToTree(convertedAmount));

        objectMapper.writeValue(res.getOutputStream(), objectNode);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }

    private ExchangeRate getRealExchangeRate(String codeFrom, String codeTo) {

        ExchangeRate exchangeRate = currencyService.getExchangeRate(codeFrom, codeTo);

        if (exchangeRate == null) {

            String temp = codeFrom;
            codeFrom = codeTo;
            codeTo = temp;

            exchangeRate = currencyService.getExchangeRate(codeFrom, codeTo);
            double rate = 1 / exchangeRate.getRate();
            exchangeRate.setRate(rate);
        }

        return exchangeRate;
    }
}
