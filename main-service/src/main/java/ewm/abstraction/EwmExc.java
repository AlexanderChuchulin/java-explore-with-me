package ewm.abstraction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class EwmExc extends RuntimeException {
    private String message;
    private String reason;
}
