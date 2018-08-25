package eleks.mentorship.bigbang.common.exception;

/**
 * Created by Emiliia Nesterovych on 8/25/2018.
 */
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
