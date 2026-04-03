package rinhacampusiv.api.v2.domain.tournaments.registrations;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import rinhacampusiv.api.v2.domain.tournaments.players.PlayerRole;

public record PlayerRegisterData(
        @NotBlank(message = "O campo nome do jogador é obrigatório")
        String playerName,

        @JsonAlias("matricula")
        String schoolId,


        @NotBlank(message = "O campo nickname é obrigatório")
        String nickname,

        @JsonAlias("discordUser")
        @NotBlank(message = "O campo discord é obrigatório")
        String discord,

        @JsonAlias("role")
        @NotNull(message = "O campo posição é obrigatório")
        PlayerRole role,

        @JsonAlias("isExternalPlayer")
        @NotNull(message = "O campo JogadorExterno é obrigatório")
        Boolean externalPlayer) {
}
