package currency_exchange_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final CurrencyDAO currencyDAO = new CurrencyDAOImpl();
    private final CurrencyService currencyService = new CurrencyServiceImpl(currencyDAO);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        try {

            String pathInfo = req.getPathInfo().replace("/", "");
            String codeBase = pathInfo.substring(0, 3).toUpperCase();
            String codeTarget = pathInfo.substring(3).toUpperCase();

            if (!(Validation.isCodeValid(codeBase) && Validation.isCodeValid(codeTarget))) {
                throw new InvalidParameterException("specified currency codes are not valid");
            }

            Currency baseCurrency = currencyService.getCurrencyByCode(codeBase);
            Currency targetCurrency = currencyService.getCurrencyByCode(codeTarget);

            ExchangeRate exchangeRate = currencyService.getExchangeRate(baseCurrency, targetCurrency);
            objectMapper.writeValue(res.getOutputStream(), exchangeRate);

        } catch (InvalidParameterException error) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", error.getMessage()));

        } catch (MissingCurrencyException | MissingCurrencyPairException error) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", error.getMessage()));

        } catch (SQLException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "Database is not available"));
        }
    }

    private void doPatch(HttpServletRequest req, HttpServletResponse res) throws IOException {

        try {

            String pathInfo = req.getPathInfo().replace("/", "");

            String codeBase = pathInfo.substring(0, 3).toUpperCase();
            String codeTarget = pathInfo.substring(3).toUpperCase();

            if (!(Validation.isCodeValid(codeBase) && Validation.isCodeValid(codeTarget))) {
                throw new InvalidParameterException("specified currency codes are not valid");
            }

            Map<String, String[]> parameterMap = req.getParameterMap();
            Set<String> paramNames = parameterMap.keySet();

            String rateStr = null;
            for (String paramName : paramNames) {

                if (paramName.equals("rate")) {
                    rateStr = parameterMap.get(paramName)[0];
                }
            }

            Currency baseCurrency = currencyService.getCurrencyByCode(codeBase);
            Currency targetCurrency = currencyService.getCurrencyByCode(codeTarget);

            ExchangeRate exchangeRate = currencyService.getExchangeRate(baseCurrency, targetCurrency);

            int id = exchangeRate.getId();
            double rate = Double.parseDouble(rateStr);

            currencyService.updateExchangeRate(id, rate);

            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "the resource updated successfully."));


        } catch (MissingCurrencyException | MissingCurrencyPairException error) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", error.getMessage()));

        } catch (InvalidParameterException | NumberFormatException | NullPointerException error) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", error.getMessage()));

        } catch (SQLException error) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "failed to update the resource, due to a database error."));
        }
    }
}
