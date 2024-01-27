package currency.exchange.api.exception;

public class CurrencyException extends IndexOutOfBoundsException {
    int code;
    private String message;

    public CurrencyException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
