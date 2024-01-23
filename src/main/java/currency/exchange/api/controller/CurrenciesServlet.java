package currency.exchange.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import currency.exchange.api.dao.CurrencyDAO;
import currency.exchange.api.dao.CurrencyRepository;
import currency.exchange.api.exception.InvalidParameterException;
import currency.exchange.api.model.Currency;
import currency.exchange.api.service.CurrencyService;
import currency.exchange.api.util.Validation;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyRepository repository = new CurrencyDAO();
    private final CurrencyService currencyService = new CurrencyService(repository);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        try {

            List<Currency> currencyList = currencyService.findAll();
            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), currencyList);

        } catch (SQLException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "database is not available."));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        try {

            String code = req.getParameter("code").toUpperCase();
            String name = req.getParameter("name");
            String sign = req.getParameter("sign");

            System.out.println(name + " " + code + " " + sign);
            if (!Validation.isCodeValid(code) || name.length() < 2 || sign.length() == 0) {
                throw new InvalidParameterException("Invalid parameter");
            }

            Currency currency = new Currency(name, code, sign);

            currencyService.save(currency);

            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "Currency saved successfully."));

        } catch (InvalidParameterException | NullPointerException error) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "Invalid parameter"));

        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                res.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "currency with this code already exists."));
            } else {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "Failed to save currency due to a database error."));
            }
        }
    }
}
