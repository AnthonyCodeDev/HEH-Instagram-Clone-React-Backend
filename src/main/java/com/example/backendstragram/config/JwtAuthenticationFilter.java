package com.example.backendstragram.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        // Log pour le débogage de l'en-tête d'autorisation
        log.info("URL demandée: {}", request.getRequestURI());
        log.info("En-tête Authorization: {}", authHeader);

        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("En-tête Authorization manquant ou invalide (doit commencer par 'Bearer ')");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            userEmail = jwtService.extractUsername(jwt);
            log.info("Email extrait du token: {}", userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                    log.info("Utilisateur trouvé dans la base: {}", userEmail);

                    if (jwtService.isTokenValid(jwt, userEmail)) {
                        log.info("Token JWT valide pour l'utilisateur: {}", userEmail);
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.info("Authentification réussie pour: {}", userEmail);
                    } else {
                        log.warn("Token JWT invalide pour l'utilisateur: {}", userEmail);
                    }
                } catch (Exception e) {
                    log.error("Erreur lors du chargement des détails utilisateur: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction de l'email du token: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
