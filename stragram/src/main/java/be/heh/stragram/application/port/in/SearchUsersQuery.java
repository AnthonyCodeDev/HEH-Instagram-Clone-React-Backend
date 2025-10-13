package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.User;

import java.util.List;

public interface SearchUsersQuery {
    
    List<User> search(String query, int page, int size);
}
