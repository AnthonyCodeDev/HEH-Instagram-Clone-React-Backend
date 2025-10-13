package be.heh.stragram.testutil;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.ArrayList;
import java.util.List;

public final class JwtTestUtils {
    
    public static RequestPostProcessor jwtUser(String userId, String role) {
        return request -> {
            request.addHeader("Authorization", "Bearer dummy-token");
            request.setUserPrincipal(() -> userId);
            return request;
        };
    }
    
    public static RequestPostProcessor withUser(String username, String... roles) {
        return SecurityMockMvcRequestPostProcessors.user(username).roles(roles);
    }
    
    public static RequestPostProcessor withAdmin() {
        return SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN");
    }
}
