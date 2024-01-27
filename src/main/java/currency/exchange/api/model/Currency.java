package currency.exchange.api.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Currency {
    @EqualsAndHashCode.Exclude
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
