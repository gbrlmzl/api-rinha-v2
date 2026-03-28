package rinhacampusiv.api.v2.domain.user;

public record LoginData(String username, String password, boolean keepLoggedIn) {
}
