package currency_exchange_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import currency_exchange_api.dao.CurrencyDAO;
import currency_exchange_api.dao.CurrencyDAOImpl;
import currency_exchange_api.exception.InvalidParameterException;
import currency_exchange_api.exception.MissingCurrencyException;
import currency_exchange_api.model.Currency;
import currency_exchange_api.service.CurrencyService;
import currency_exchange_api.service.CurrencyServiceImpl;
import currency_exchange_api.util.Validation;
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

    private final CurrencyDAO currencyDAO = new CurrencyDAOImpl();
    private final CurrencyService currencyService = new CurrencyServiceImpl(currencyDAO);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        try {

            List<Currency> currencyList = currencyService.getCurrencies();
            res.setContentType("application/json");
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

            if (!Validation.isCodeValid(code) || name.length() < 2 || sign.length() == 0) {
                System.out.println(name + " " + code + " " + sign);
                throw new InvalidParameterException("Invalid parameter");
            }

            currencyService.saveCurrency(name, code, sign);

            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "Currency saved successfully."));

        } catch (InvalidParameterException error) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", error.getMessage()));

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
