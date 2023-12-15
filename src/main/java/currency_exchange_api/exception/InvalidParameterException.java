package currency_exchange_api.exception;

public class InvalidParameterException extends Exception {

    private String message;

    public InvalidParameterException(String message) {
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