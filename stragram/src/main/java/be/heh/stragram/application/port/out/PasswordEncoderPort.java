package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.value.PasswordHash;

public interface PasswordEncoderPort {
    
    PasswordHash encode(String rawPassword);
    
    boolean matches(String rawPassword, PasswordHash encodedPassword);
}
