package currency_exchange_api.exception;

public class MissingCurrencyException extends IndexOutOfBoundsException {

    private String message;

    public MissingCurrencyException(String message) {
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
