package currency.exchange.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import currency.exchange.api.exception.CurrencyException;
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
import java.util.Collections;

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
            if (pathInfo.length() != 6) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "specified currency codes are not valid or absent"));
            }
            String codeBase = pathInfo.substring(0, 3).toUpperCase();
            String codeTarget = pathInfo.substring(3).toUpperCase();
            if (!(Validation.isCodeValid(codeBase) && Validation.isCodeValid(codeTarget))) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "specified currency codes are not valid or absent"));
            }
            ExchangeRate exchangeRate = currencyService.findByCurrencyCodes(codeBase, codeTarget);
            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), exchangeRate);
        } catch (CurrencyException error) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", error.getMessage()));
        }
    }

    private void doPatch(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            String pathInfo = req.getPathInfo().replace("/", "");
            if (pathInfo.length() != 6) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "specified currency codes are not valid or absent"));
            }
            String codeBase = pathInfo.substring(0, 3).toUpperCase();
            String codeTarget = pathInfo.substring(3).toUpperCase();
            if (!(Validation.isCodeValid(codeBase) && Validation.isCodeValid(codeTarget))) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "specified currency codes are not valid or absent"));
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
        } catch (CurrencyException error) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", error.getMessage()));
        }
    }
}
