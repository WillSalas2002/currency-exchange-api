package currency.exchange.api.model;

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

    public Currency(int id) {
        this.id = id;
    }

    public Currency(String code, String fullName, String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }
}
