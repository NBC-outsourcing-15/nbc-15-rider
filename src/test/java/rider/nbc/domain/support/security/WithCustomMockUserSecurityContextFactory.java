package rider.nbc.domain.support.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import rider.nbc.domain.user.entity.Role;
import rider.nbc.global.auth.AuthUser;

import java.util.List;

public class WithCustomMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {
    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        long id = annotation.id();
        String email = annotation.email();
        Role role = annotation.role();

        AuthUser user = AuthUser.builder()
                .id(id)
                .email(email)
                .role(role)
                .build();

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user, null
                        , List.of(new SimpleGrantedAuthority(annotation.role().name())));
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }
}
