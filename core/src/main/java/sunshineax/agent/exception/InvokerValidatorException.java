package sunshineax.agent.exception;

import tools.jackson.databind.ObjectMapper;

public class InvokerValidatorException extends RuntimeException {

    private Object constraints;

    public InvokerValidatorException(String message) {
        super(message);
    }

    public InvokerValidatorException(String message, Object constraints) {
        super(message);
        this.constraints = constraints;
    }

    public Object getConstraints() {
        return constraints;
    }

    public String normalizeValue() {
        return new ObjectMapper().writeValueAsString(constraints);
    }
}
