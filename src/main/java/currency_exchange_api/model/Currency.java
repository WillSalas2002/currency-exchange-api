package currency_exchange_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Currency {

    private int id;
    private String code;
    private String fullName;
    private String sign;
}
