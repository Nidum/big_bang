package eleks.mentorship.bigbang.common.exception;

public class UnsupportedMessageTypeException extends RuntimeException {
    public UnsupportedMessageTypeException() {
    }

    public UnsupportedMessageTypeException(String message) {
        super(message);
    }

    public UnsupportedMessageTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedMessageTypeException(Throwable cause) {
        super(cause);
    }
}
