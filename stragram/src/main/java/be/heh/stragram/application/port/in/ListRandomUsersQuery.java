package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.User;

import java.util.List;

public interface ListRandomUsersQuery {

    /**
     * Retourne jusqu'à `size` users choisis aléatoirement.
     */
    List<User> listRandomUsers(int size);
}
