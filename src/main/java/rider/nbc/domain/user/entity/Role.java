package rider.nbc.domain.user.entity;

public enum Role {
    USER,
    CEO;

    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
