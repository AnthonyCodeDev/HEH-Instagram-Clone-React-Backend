package be.heh.stragram.application.port.out;

import be.heh.stragram.application.domain.model.User;

import java.util.List;

public interface SearchUsersPort {
    
    List<User> searchByUsernameOrBio(String query, int page, int size);
}
