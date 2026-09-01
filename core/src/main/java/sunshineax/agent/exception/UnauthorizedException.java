package sunshineax.agent.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {

    private String reason;

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
