package rinhacampusiv.api.v2.domain.auth;

public record AuthenticatedUserDataResponse(
        String username,
        String nickname,
        String email,
        String profilePic,
        String role
) {
}
