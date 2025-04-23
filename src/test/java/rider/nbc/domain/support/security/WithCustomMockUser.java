package rider.nbc.domain.support.security;

import org.springframework.security.test.context.support.WithSecurityContext;
import rider.nbc.domain.user.entity.Role;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactory.class)
public @interface WithCustomMockUser {
    long id() default 1L;

    String email() default "test@test";

    Role role() default Role.USER;
}
