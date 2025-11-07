package be.heh.stragram.application.port.in;

import be.heh.stragram.application.domain.model.Post;
import be.heh.stragram.application.domain.value.UserId;

import java.util.List;

public interface ListRecentPostsQuery {
    
    /**
     * Récupère les posts les plus récents, triés par date de création décroissante.
     * 
     * @param page Le numéro de page (commence à 0)
     * @param size Le nombre de posts par page
     * @param currentUserId L'ID de l'utilisateur actuel (peut être null pour les utilisateurs non authentifiés)
     * @return Une liste de posts
     */
    List<Post> listRecentPosts(int page, int size, UserId currentUserId);
}
