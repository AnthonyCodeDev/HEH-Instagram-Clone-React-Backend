package be.heh.stragram.adapter.out.crypto;

import be.heh.stragram.application.domain.value.PasswordHash;
import be.heh.stragram.application.port.out.PasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public PasswordHash encode(String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        return PasswordHash.of(encodedPassword);
    }

    @Override
    public boolean matches(String rawPassword, PasswordHash encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword.getValue());
    }
}
