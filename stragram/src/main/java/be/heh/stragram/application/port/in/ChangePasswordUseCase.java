package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.value.UserId;
import lombok.Builder;
import lombok.Value;

public interface ChangePasswordUseCase {

    void changePassword(UserId userId, ChangePasswordCommand command);

    @Value
    @Builder
    class ChangePasswordCommand {
        String currentPassword;
        String newPassword;
    }
}
