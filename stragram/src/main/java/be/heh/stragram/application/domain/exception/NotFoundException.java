package be.heh.stragram.application.domain.exception;

public class NotFoundException extends DomainException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String entityType, String identifier) {
        super(String.format("%s with identifier %s not found", entityType, identifier));
    }
}
