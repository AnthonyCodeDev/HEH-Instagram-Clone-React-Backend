package be.heh.stragram.application.domain.value;

import be.heh.stragram.application.domain.exception.ValidationException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
public class Username {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._]{3,30}$");
    
    private final String value;

    private Username(String value) {
        this.value = value;
    }

    public static Username of(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        
        String trimmedUsername = username.trim();
        
        if (!USERNAME_PATTERN.matcher(trimmedUsername).matches()) {
            throw new ValidationException("Username must be 3-30 characters and contain only letters, numbers, dots, and underscores");
        }
        
        return new Username(trimmedUsername);
    }

    @Override
    public String toString() {
        return value;
    }
}
