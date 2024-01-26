package currency.exchange.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import currency.exchange.api.exception.MissingCurrencyException;
import currency.exchange.api.model.Currency;
import currency.exchange.api.model.ExchangeRate;
import currency.exchange.api.service.CurrencyService;
import currency.exchange.api.service.ExchangeRateService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            List<ExchangeRate> exchangeRatesList = exchangeRateService.findAll();
            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), exchangeRatesList);
        } catch (SQLException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "Database is not available"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            String baseCurrencyCode = req.getParameter("baseCurrencyCode").toUpperCase();
            String targetCurrencyCode = req.getParameter("targetCurrencyCode").toUpperCase();
            String rateStr = req.getParameter("rate").replace(",", ".");
            BigDecimal rate = new BigDecimal(rateStr);
            Optional<Currency> baseCurrency = currencyService.findByCode(baseCurrencyCode);
            Optional<Currency> targetCurrency = currencyService.findByCode(targetCurrencyCode);
            if (baseCurrency.isPresent() && targetCurrency.isPresent()) {
                ExchangeRate exchangeRate = new ExchangeRate(baseCurrency.get(), targetCurrency.get(), rate);
                exchangeRateService.save(exchangeRate);
                res.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "exchange rate saved successfully."));
            } else {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "specified currency pair is absent in database"));
            }
        } catch (NumberFormatException | NullPointerException error) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "invalid parameters"));
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                res.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "exchange rate with this currency pairs already exists."));
            } else {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "Failed to save currency due to a database error."));
            }
        }
    }
}
