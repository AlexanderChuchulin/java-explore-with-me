package ewm.exception;

import ewm.abstraction.EwmExc;

public class EntityNotFoundExc extends EwmExc {

    public EntityNotFoundExc(String message, String reason) {
        super(message, reason);
    }
}
