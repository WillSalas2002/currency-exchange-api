package currency.exchange.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import currency.exchange.api.dao.CurrencyDAO;
import currency.exchange.api.dao.CurrencyRepository;
import currency.exchange.api.exception.InvalidParameterException;
import currency.exchange.api.exception.MissingCurrencyException;
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

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyRepository repository = new CurrencyDAO();
    private final CurrencyService currencyService = new CurrencyService(repository);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        try {

            String code = req.getPathInfo().replace("/", "").toUpperCase();

            if (!Validation.isCodeValid(code)) {
                throw new InvalidParameterException("specified currency code is not valid or it is absent.");
            }

            Currency currency = currencyService.findByCode(code);
            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), currency);

        } catch (InvalidParameterException | NullPointerException error) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "specified currency code is not valid or it is absent."));

        } catch (MissingCurrencyException error) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", error.getMessage()));

        } catch (SQLException e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(res.getWriter(), Collections.singletonMap("message", "Database is not available."));
        }
    }
}
