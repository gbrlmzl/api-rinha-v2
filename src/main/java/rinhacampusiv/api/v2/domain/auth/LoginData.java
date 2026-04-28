package rinhacampusiv.api.v2.domain.auth;

public record LoginData(String username, String password, boolean keepLoggedIn) {
}
