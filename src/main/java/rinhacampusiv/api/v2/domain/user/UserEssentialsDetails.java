package rinhacampusiv.api.v2.domain.user;

import java.util.UUID;

public record UserEssentialsDetails(Long id, String nickname, String username, String email, String profilePic) {

    public UserEssentialsDetails(User user){
        this(user.getId(), user.getNickname(), user.getUsername(), user.getEmail(), user.getProfilePic());
    }
}
