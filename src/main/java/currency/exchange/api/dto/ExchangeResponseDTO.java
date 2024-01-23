package currency.exchange.api.dto;

import currency.exchange.api.model.ExchangeRate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Setter
@Getter
public class ExchangeResponseDTO {

    private ExchangeRate exchangeRate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
}
