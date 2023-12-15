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

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyDAO currencyDAO = new CurrencyDAOImpl();
    private final CurrencyService currencyService = new CurrencyServiceImpl(currencyDAO);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        try {

            String code = req.getPathInfo().replace("/", "");

            if (!Validation.isCodeValid(code)) {
                throw new InvalidParameterException("specified currency code is not valid or it is absent.");
            }

            Currency currency = currencyService.getCurrencyByCode(code);
            res.setContentType("application/json");
            objectMapper.writeValue(res.getOutputStream(), currency);

        } catch (InvalidParameterException error) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", error.getMessage()));

        } catch (MissingCurrencyException error) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", error.getMessage()));

        } catch (SQLException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "Database is not available."));
        }
    }
}
