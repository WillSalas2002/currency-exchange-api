package currency_exchange_api.dto;

import currency_exchange_api.model.ExchangeRate;
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
