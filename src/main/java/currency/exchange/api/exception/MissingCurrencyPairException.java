package currency.exchange.api.exception;

public class MissingCurrencyPairException extends ArrayIndexOutOfBoundsException {
    private String message;

    public MissingCurrencyPairException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
