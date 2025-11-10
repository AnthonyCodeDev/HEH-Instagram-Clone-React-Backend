package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.Message;

public interface SaveMessagePort {
    Message save(Message message);
}
