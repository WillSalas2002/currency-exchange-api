package currency.exchange.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import currency.exchange.api.exception.CurrencyException;
import currency.exchange.api.model.Currency;
import currency.exchange.api.service.CurrencyService;
import currency.exchange.api.util.Validation;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            String code = req.getPathInfo().replace("/", "").toUpperCase();
            if (!Validation.isCodeValid(code)) {
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "specified currency code is not valid or it is absent."));
                return;
            }
            Optional<Currency> currencyOptional = currencyService.findByCode(code);
            if (currencyOptional.isEmpty()) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "currency not found."));
                return;
            }
            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), currencyOptional.get());
        } catch (CurrencyException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(res.getWriter(), e.getMessage());
        }
    }
}
