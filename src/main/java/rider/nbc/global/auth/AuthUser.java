package rider.nbc.global.auth;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rider.nbc.domain.user.entity.Role;

@Getter
@RequiredArgsConstructor
@Builder
public class AuthUser {
    private final Long id;
    private final String email;
    private final Role role;
}