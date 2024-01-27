package currency.exchange.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import currency.exchange.api.dto.ExchangeResponse;
import currency.exchange.api.exception.CurrencyException;
import currency.exchange.api.service.ExchangeRateService;
import currency.exchange.api.util.Validation;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            String codeFrom = null;
            String codeTo = null;
            String amountStr = null;
            Map<String, String[]> parameterMap = req.getParameterMap();
            Set<String> paramNames = parameterMap.keySet();
            for (String paramName : paramNames) {
                String[] values = parameterMap.get(paramName);
                switch (paramName) {
                    case "from" -> {
                        codeFrom = values[0].toUpperCase();
                    }
                    case "to" -> {
                        codeTo = values[0].toUpperCase();
                    }
                    case "amount" -> {
                        amountStr = values[0];
                    }
                }
            }
            if (codeFrom == null || codeTo == null || amountStr == null ||
                    codeTo.isBlank() || codeFrom.isBlank() || amountStr.isBlank()) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "invalid parameter(s)"));
                return;
            }
            if (!(Validation.isCodeValid(codeTo) && Validation.isCodeValid(codeFrom))) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "currency code is not valid"));
            }
            BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(amountStr));
            ExchangeResponse exchangeResponse = exchangeRateService.calculateExchangeRate(codeFrom, codeTo, amount);
            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), exchangeResponse);
        } catch (CurrencyException error) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", error.getMessage()));
        }
    }
}
