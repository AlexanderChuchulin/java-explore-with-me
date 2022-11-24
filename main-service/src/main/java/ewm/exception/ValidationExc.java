package ewm.exception;

import ewm.abstraction.EwmExc;

public class ValidationExc extends EwmExc {
    public ValidationExc(String message, String reason) {
        super(message, reason);
    }
}
