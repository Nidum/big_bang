package eleks.mentorship.bigbang.exception;

public class MessageFromUnknownUserException extends RuntimeException {
    public MessageFromUnknownUserException() {
    }

    public MessageFromUnknownUserException(String message) {
        super(message);
    }
}
