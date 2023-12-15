package currency_exchange_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import currency_exchange_api.dao.CurrencyDAO;
import currency_exchange_api.dao.CurrencyDAOImpl;
import currency_exchange_api.model.ExchangeRate;
import currency_exchange_api.service.CurrencyService;
import currency_exchange_api.service.CurrencyServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private CurrencyDAO currencyDAO = new CurrencyDAOImpl();
    private CurrencyService currencyService = new CurrencyServiceImpl(currencyDAO);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String pathInfo = req.getPathInfo();
        String code1 = pathInfo.substring(pathInfo.length() - 6, pathInfo.length() - 3).toUpperCase();
        String code2 = pathInfo.substring(pathInfo.length() - 3).toUpperCase();

        ExchangeRate exchangeRate = currencyService.getExchangeRate(code1, code2);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(res.getOutputStream(), exchangeRate);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        }
    }

    private void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String pathInfo = req.getPathInfo();
        String code1 = pathInfo.substring(pathInfo.length() - 6, pathInfo.length() - 3).toUpperCase();
        String code2 = pathInfo.substring(pathInfo.length() - 3).toUpperCase();

        Map<String, String[]> parameterMap = req.getParameterMap();
        Set<String> paramNames = parameterMap.keySet();

        String rateStr = null;
        for (String paramName : paramNames) {

            if (paramName.equals("rate")) {
                rateStr = parameterMap.get(paramName)[0];
            }
        }

        ExchangeRate exchangeRate = currencyService.getExchangeRate(code1, code2);

        int id = exchangeRate.getId();
        double rate = Double.parseDouble(rateStr);

        currencyService.updateExchangeRate(id, rate);
        PrintWriter pr = resp.getWriter();
        pr.write("All good!");
    }
}
