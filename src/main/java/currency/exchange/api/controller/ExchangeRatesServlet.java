package currency.exchange.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import currency.exchange.api.exception.CurrencyException;
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
        } catch (CurrencyException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "Database is not available"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            String baseCurrencyCode = req.getParameter("baseCurrencyCode");
            String targetCurrencyCode = req.getParameter("targetCurrencyCode");
            String rateStr = req.getParameter("rate").replace(",", ".");
            boolean matches = rateStr.matches("\\d+");
            if (baseCurrencyCode == null || targetCurrencyCode == null || !matches ||
                    baseCurrencyCode.isBlank() || targetCurrencyCode.isBlank() || rateStr.isBlank()) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "invalid parameters"));
                return;
            }
            BigDecimal rate = new BigDecimal(rateStr);
            Optional<Currency> baseCurrency = currencyService.findByCode(baseCurrencyCode.toUpperCase());
            Optional<Currency> targetCurrency = currencyService.findByCode(targetCurrencyCode.toUpperCase());
            if (baseCurrency.isEmpty() || targetCurrency.isEmpty()) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "specified currency pair is absent in database"));
                return;
            }
            ExchangeRate exchangeRate = new ExchangeRate(baseCurrency.get(), targetCurrency.get(), rate);
            exchangeRateService.save(exchangeRate);
            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "exchange rate saved successfully."));

        } catch (CurrencyException e) {
            if (e.getCode() == 19) {
                res.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", e.getMessage()));
            } else {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "Failed to save currency due to a database error."));
            }
        }
    }
}
