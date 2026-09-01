package sunshineax.agent.exception;

public class TransformException extends RuntimeException {

    public TransformException(String message) {
        super(message);
    }

    public TransformException(String message, Exception exception) {
        super(message, exception);
    }
}
