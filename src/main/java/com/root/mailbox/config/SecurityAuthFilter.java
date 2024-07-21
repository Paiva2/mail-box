package com.root.mailbox.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.root.mailbox.domain.entities.User;
import com.root.mailbox.domain.exceptions.user.UserNotFoundException;
import com.root.mailbox.infra.providers.UserDataProvider;
import com.root.mailbox.presentation.adapters.JwtAdapter;
import com.root.mailbox.presentation.adapters.UserDetailsAdapter;
import com.root.mailbox.presentation.exceptions.TokenNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
public class SecurityAuthFilter extends OncePerRequestFilter {
    private static List<AntPathRequestMatcher> UNFILTERABLE_ROUTES;

    private final UserDataProvider userDataProvider;
    private final JwtAdapter jwtAdapter;

    static {
        UNFILTERABLE_ROUTES = new ArrayList<>() {{
            add(new AntPathRequestMatcher("/api/v1/user/register", HttpMethod.POST.toString()));
            add(new AntPathRequestMatcher("/api/v1/user/login", HttpMethod.POST.toString()));
            add(new AntPathRequestMatcher("/api/v1/user/login", HttpMethod.POST.toString()));
            add(new AntPathRequestMatcher("/api/v1/user/forgot-password", HttpMethod.PATCH.toString()));

            add(new AntPathRequestMatcher("/ws/info", HttpMethod.POST.toString()));
            add(new AntPathRequestMatcher("/ws/info", HttpMethod.GET.toString()));
            add(new AntPathRequestMatcher("/ws/**", HttpMethod.POST.toString()));
            add(new AntPathRequestMatcher("/ws/**", HttpMethod.GET.toString()));
        }};
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String token = getHeaderToken(request);

        if (Objects.isNull(token)) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "Authentication token not present on Authorization header");
            return;
        }

        DecodedJWT jwt;
        Long subjectId;

        try {
            jwt = jwtAdapter.verify(token);
            subjectId = Long.valueOf(jwt.getSubject());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.sendError(HttpStatus.FORBIDDEN.value(), "Error while verifying JWT token...");
            return;
        }

        Optional<User> user = userDataProvider.findUserById(subjectId);

        if (user.isEmpty()) {
            response.sendError(HttpStatus.NOT_FOUND.value(), "User not found");
            return;
        }

        UserDetails userDetails = UserDetailsAdapter.builder()
            .id(user.get().getId())
            .password(user.get().getPassword())
            .role(user.get().getRole())
            .isEnabled(!user.get().getDisabled())
            .build();

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return UNFILTERABLE_ROUTES.stream().anyMatch(matcher -> matcher.matches(request));
    }

    private String getHeaderToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (Objects.isNull(token)) return null;

        return token.replace("Bearer ", "");
    }
}
