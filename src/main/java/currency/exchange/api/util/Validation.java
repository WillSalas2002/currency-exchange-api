package currency.exchange.api.util;

public class Validation {

    public static boolean isCodeValid(String code) {

        if (code == null) {
            return false;
        }

        if (code.length() != 3) {
            return false;
        }

        for (int i = 0; i < code.length(); i++) {

            if (code.charAt(i) < 'A' || code.charAt(i) > 'z') {
                return false;
            }
        }
        return true;
    }
}
