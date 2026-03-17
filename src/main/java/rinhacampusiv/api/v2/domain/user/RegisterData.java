package rinhacampusiv.api.v2.domain.user;

import jakarta.validation.constraints.NotBlank;

public record RegisterData(@NotBlank
                           String nickname,
                           @NotBlank
                           String username,
                           @NotBlank
                           String email,
                           @NotBlank
                           String password,
                           String profilePic) {
}
