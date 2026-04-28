package rinhacampusiv.api.v2.domain.user;

public record UserEssentialsDetails(Long id, String nickname, String username, String email, String profilePic, UserRole role) {

    public UserEssentialsDetails(User user){
        this(user.getId(), user.getNickname(), user.getUsername(), user.getEmail(), user.getProfilePic(), user.getRole());
    }
}
