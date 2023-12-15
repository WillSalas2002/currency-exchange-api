package currency_exchange_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import currency_exchange_api.dao.CurrencyDAO;
import currency_exchange_api.dao.CurrencyDAOImpl;
import currency_exchange_api.exception.InvalidParameterException;
import currency_exchange_api.exception.MissingCurrencyException;
import currency_exchange_api.model.ExchangeRate;
import currency_exchange_api.service.CurrencyService;
import currency_exchange_api.service.CurrencyServiceImpl;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final CurrencyDAO currencyDAO = new CurrencyDAOImpl();
    private final CurrencyService currencyService = new CurrencyServiceImpl(currencyDAO);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        try {

            List<ExchangeRate> exchangeRatesList = currencyService.getExchangeRates();
            res.setContentType("application/json");
            objectMapper.writeValue(res.getOutputStream(), exchangeRatesList);

        } catch (SQLException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "Database is not available"));
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        try {

            String baseCurrencyCode = req.getParameter("baseCurrencyCode");
            String targetCurrencyCode = req.getParameter("targetCurrencyCode");
            String rateStr = req.getParameter("rate");

            double rate = Double.parseDouble(rateStr);

            currencyService.saveExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);

            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "exchange rate saved successfully."));

        } catch (NumberFormatException | NullPointerException error) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "invalid rate"));

        } catch (MissingCurrencyException error) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", error.getMessage()));

        } catch (SQLException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "Database is not available."));
        }

    }
}
