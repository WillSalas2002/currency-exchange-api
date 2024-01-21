package currency_exchange_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import currency_exchange_api.dao.CurrencyDAO;
import currency_exchange_api.dao.CurrencyDAOImpl;
import currency_exchange_api.dto.ExchangeResponseDTO;
import currency_exchange_api.exception.InvalidParameterException;
import currency_exchange_api.exception.MissingCurrencyException;
import currency_exchange_api.exception.MissingCurrencyPairException;
import currency_exchange_api.model.ExchangeRate;
import currency_exchange_api.service.CurrencyService;
import currency_exchange_api.service.CurrencyServiceImpl;
import currency_exchange_api.util.Validation;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
                throw new InvalidParameterException("specified currency code is not valid or it is absent.");
            }

            BigDecimal amountDouble = BigDecimal.valueOf(Double.parseDouble(amount));

            ExchangeRate exchangeRate = getRealExchangeRate(codeFrom, codeTo);

            BigDecimal rate = exchangeRate.getRate();

            BigDecimal convertedAmount = rate.multiply(amountDouble);
            convertedAmount = convertedAmount.setScale(6, RoundingMode.HALF_EVEN);

            ExchangeResponseDTO exchangeResponseDTO = new ExchangeResponseDTO(exchangeRate, amountDouble, convertedAmount);

            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), exchangeResponseDTO);

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

        ExchangeRate exchangeRate;
        try {

            exchangeRate = currencyService.getExchangeRate(codeFrom, codeTo);

        } catch (MissingCurrencyPairException e) {

            exchangeRate = currencyService.getExchangeRate(codeTo, codeFrom);
            BigDecimal rate = BigDecimal.ONE.divide(exchangeRate.getRate(), 6, RoundingMode.HALF_EVEN);
            exchangeRate.setRate(rate);
        }

        return exchangeRate;
    }
}
