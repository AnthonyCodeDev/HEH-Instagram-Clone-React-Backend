package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.User;

public interface SaveUserPort {
    
    User save(User user);
    
    void delete(User user);
}
