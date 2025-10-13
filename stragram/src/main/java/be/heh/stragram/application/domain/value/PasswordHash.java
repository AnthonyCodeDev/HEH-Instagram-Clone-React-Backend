package be.heh.stragram.application.domain.value;

import be.heh.stragram.application.domain.exception.ValidationException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class PasswordHash {
    private final String value;

    private PasswordHash(String value) {
        this.value = value;
    }

    public static PasswordHash of(String hash) {
        if (hash == null || hash.trim().isEmpty()) {
            throw new ValidationException("Password hash cannot be empty");
        }
        
        return new PasswordHash(hash);
    }

    @Override
    public String toString() {
        return "[PROTECTED]";
    }
}
