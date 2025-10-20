package be.heh.stragram.adapter.in.security;

import be.heh.stragram.application.domain.value.UserId;
import be.heh.stragram.application.port.out.TokenProviderPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProviderPort tokenProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Log pour déboguer
        log.debug("Processing request: {}", request.getRequestURI());
        
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                Optional<UserId> userIdOpt = tokenProvider.validateTokenAndGetUserId(jwt);
                
                if (userIdOpt.isPresent()) {
                    UserId userId = userIdOpt.get();
                    boolean isAdmin = tokenProvider.isAdmin(jwt);
                    
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    
                    if (isAdmin) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    }

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Ne jamais appliquer le filtre pour les endpoints d'authentification
        if (path.startsWith("/auth/")) {
            log.debug("Skipping JWT filter for auth endpoint: {}", path);
            return true;
        }
        
        // Ne pas appliquer pour d'autres endpoints publics
        boolean shouldSkip = pathMatcher.match("/swagger-ui/**", path) || 
                             pathMatcher.match("/swagger-ui.html", path) || 
                             pathMatcher.match("/v3/api-docs/**", path) || 
                             pathMatcher.match("/v3/api-docs.yaml", path) ||
                             pathMatcher.match("/webjars/**", path) || 
                             pathMatcher.match("/favicon.ico", path) ||
                             pathMatcher.match("/h2-console/**", path) ||
                             pathMatcher.match("/images/**", path);
        
        if (shouldSkip) {
            log.debug("Skipping JWT filter for public endpoint: {}", path);
        }
        
        return shouldSkip;
    }
}