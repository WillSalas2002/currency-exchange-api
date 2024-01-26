package currency.exchange.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import currency.exchange.api.repository.JdbcExchangeRateRepository;
import currency.exchange.api.repository.ExchangeRateRepository;
import currency.exchange.api.exception.InvalidParameterException;
import currency.exchange.api.exception.MissingCurrencyException;
import currency.exchange.api.exception.MissingCurrencyPairException;
import currency.exchange.api.model.ExchangeRate;
import currency.exchange.api.service.ExchangeRateService;
import currency.exchange.api.util.Validation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService currencyService = new ExchangeRateService();
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
            ExchangeRate exchangeRate = currencyService.findByCurrencyCodes(codeBase, codeTarget);
            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), exchangeRate);
        } catch (InvalidParameterException | StringIndexOutOfBoundsException | NullPointerException error) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "specified currency codes are not valid or absent"));

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
            BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
            String line;
            String rateStr = null;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split("=");
                rateStr = split[1];
            }
            ExchangeRate exchangeRate = currencyService.findByCurrencyCodes(codeBase, codeTarget);
            int id = exchangeRate.getId();
            BigDecimal rate = new BigDecimal(rateStr);
            currencyService.update(id, rate);
            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "the resource updated successfully."));
        } catch (MissingCurrencyException | MissingCurrencyPairException error) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", error.getMessage()));
        } catch (InvalidParameterException | NumberFormatException | NullPointerException |
                 StringIndexOutOfBoundsException error) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "specified currency codes are not valid or absent"));
        } catch (SQLException error) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "failed to update the resource, due to a database error."));
        }
    }
}
