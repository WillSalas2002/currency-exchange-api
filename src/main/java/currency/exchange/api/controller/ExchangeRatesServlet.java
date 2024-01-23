package currency.exchange.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import currency.exchange.api.dao.CurrencyDAO;
import currency.exchange.api.dao.CurrencyRepository;
import currency.exchange.api.dao.ExchangeRateDAO;
import currency.exchange.api.dao.ExchangeRateRepository;
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

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateRepository exchangeRateRepository = new ExchangeRateDAO();
    private final ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateRepository);
    private final CurrencyRepository currencyRepository = new CurrencyDAO();
    private final CurrencyService currencyService = new CurrencyService(currencyRepository);
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

            Currency baseCurrency = currencyService.findByCode(baseCurrencyCode);
            Currency targetCurrency = currencyService.findByCode(targetCurrencyCode);

            ExchangeRate exchangeRate = new ExchangeRate(baseCurrency, targetCurrency, rate);

            exchangeRateService.save(exchangeRate);

            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "exchange rate saved successfully."));

        } catch (NumberFormatException | NullPointerException error) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "invalid parameters"));

        } catch (MissingCurrencyException error) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "specified currency pair is absent in database"));

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
