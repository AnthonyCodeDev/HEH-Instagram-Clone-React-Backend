package be.heh.stragram.application.domain.exception;

public class UnauthorizedActionException extends DomainException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
