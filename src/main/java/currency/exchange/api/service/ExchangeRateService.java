package currency.exchange.api.service;

import currency.exchange.api.dto.ExchangeResponse;
import currency.exchange.api.exception.CurrencyException;
import currency.exchange.api.model.Currency;
import currency.exchange.api.repository.CurrencyRepository;
import currency.exchange.api.repository.ExchangeRateRepository;
import currency.exchange.api.model.ExchangeRate;
import currency.exchange.api.repository.JdbcCurrencyRepository;
import currency.exchange.api.repository.JdbcExchangeRateRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    private final ExchangeRateRepository exchangeRateRepository = new JdbcExchangeRateRepository();
    private final CurrencyRepository currencyRepository = new JdbcCurrencyRepository();

    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRateList = new ArrayList<>();
        List<ExchangeRate> list = exchangeRateRepository.findAll();
        for (ExchangeRate item : list) {
            exchangeRateList.add(toExchangeRate(item));
        }
        return exchangeRateList;
    }

    public ExchangeRate findByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<Currency> baseCurrencyOptional = currencyRepository.findByCode(baseCurrencyCode);
        Optional<Currency> targetCurrencyOptional = currencyRepository.findByCode(targetCurrencyCode);
        if (baseCurrencyOptional.isPresent() && targetCurrencyOptional.isPresent()) {
            int baseCurrencyId = baseCurrencyOptional.get().getId();
            int targetCurrencyId = targetCurrencyOptional.get().getId();
            Optional<ExchangeRate> exchangeRateOptional = exchangeRateRepository.findByCurrencyCodes(baseCurrencyId, targetCurrencyId);
            if (exchangeRateOptional.isPresent()) {
                return toExchangeRate(exchangeRateOptional.get());
            } else {
                // If one of the currencies is not found, the currency-pair exception will be thrown
                throw new CurrencyException("specified currency pair does not exist in the database");
            }
        } else {
            // If one of the currencies is not found, the currency-pair exception will be thrown
            throw new CurrencyException("specified currency pair does not exist in the database");
    }
}

    public void save(ExchangeRate exchangeRate) {
        exchangeRateRepository.save(exchangeRate);
    }

    public void update(int id, BigDecimal rate) {
        exchangeRateRepository.update(id, rate);
    }

    private ExchangeRate toExchangeRate(ExchangeRate exchangeRate) {
        return new ExchangeRate(
                exchangeRate.getId(),
                fetch(exchangeRate.getBaseCurrency()),
                fetch(exchangeRate.getTargetCurrency()),
                exchangeRate.getRate()
        );
    }

    private Currency fetch(Currency currency) {
        return currencyRepository.findById(currency.getId());
    }

    public ExchangeResponse calculateExchangeRate(String codeFrom, String codeTo, BigDecimal amount) {
        ExchangeResponse exchangeResponse = null;
        ExchangeRate exchangeRate = getRealExchangeRate(codeFrom, codeTo);
        BigDecimal rate = exchangeRate.getRate();
        BigDecimal convertedAmount = rate.multiply(amount);
        convertedAmount = convertedAmount.setScale(6, RoundingMode.HALF_EVEN);
        Optional<Currency> codeFromOptional = currencyRepository.findByCode(codeFrom);
        Optional<Currency> codeToOptional = currencyRepository.findByCode(codeTo);
        if (codeFromOptional.isPresent() &&  codeToOptional.isPresent()) {
            exchangeResponse = new ExchangeResponse(codeFromOptional.get(), codeToOptional.get(), exchangeRate.getRate(), amount, convertedAmount);
        }
        return exchangeResponse;
    }

    private ExchangeRate getRealExchangeRate(String codeFrom, String codeTo) {
        ExchangeRate exchangeRate;
        try {
            exchangeRate = findByCurrencyCodes(codeFrom, codeTo);
        } catch (CurrencyException e) {
            ExchangeRate exchangeRateTemp = findByCurrencyCodes(codeTo, codeFrom);
            exchangeRateTemp.setRate(BigDecimal.ONE.divide(exchangeRateTemp.getRate(), 6, RoundingMode.HALF_DOWN));
            return exchangeRateTemp;
        }
        return exchangeRate;
    }
}