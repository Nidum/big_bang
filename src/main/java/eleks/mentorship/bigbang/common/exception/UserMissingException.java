package eleks.mentorship.bigbang.common.exception;

/**
 * Created by Emiliia Nesterovych on 8/26/2018.
 */
public class UserMissingException extends RuntimeException {
    public UserMissingException() {
    }

    public UserMissingException(String message) {
        super(message);
    }

    public UserMissingException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserMissingException(Throwable cause) {
        super(cause);
    }
}
