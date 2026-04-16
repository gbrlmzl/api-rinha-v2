package rinhacampusiv.api.v2.domain.tournaments.tournaments;

import org.springframework.stereotype.Component;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentPublicDetailData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.TournamentPublicSummaryData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentAdminDetailData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentAdminSummaryData;

@Component
public class TournamentMapper {

    // Para a Tabela do Admin
    public TournamentAdminSummaryData toAdminSummaryData(Tournament tournament, long confirmedTeamsCount) {
        return new TournamentAdminSummaryData(
                tournament.getId(),
                tournament.getName(),
                tournament.getGame(),
                tournament.getStatus(),
                confirmedTeamsCount + "/" + tournament.getMaxTeams(),
                tournament.getPrizePool(),
                tournament.getStartsAt(),
                tournament.getEndsAt(),
                tournament.getImageUrl()
        );
    }

    // Para o Formulário de Edição do Admin
    public TournamentAdminDetailData toAdminDetailData(Tournament tournament) {
        return new TournamentAdminDetailData(
                tournament.getId(),
                tournament.getName(),
                tournament.getGame(),
                tournament.getStatus(),
                tournament.getMaxTeams(),
                tournament.getPrizePool(),
                tournament.getStartsAt(),
                tournament.getEndsAt(),
                tournament.getCreatedAt(),
                tournament.getDescription(),
                tournament.getImageUrl(),
                tournament.getRulesUrl()
        );
    }

    // Para o Carrossel Público
    public TournamentPublicSummaryData toPublicSummaryData(Tournament tournament, long confirmedTeamsCount) {
        return new TournamentPublicSummaryData(
                tournament.getId(),
                tournament.getName(),
                tournament.getGame(),
                tournament.getStatus(),
                confirmedTeamsCount + "/" + tournament.getMaxTeams(),
                tournament.getStartsAt(),
                tournament.getPrizePool(),
                tournament.getImageUrl(),
                tournament.getRulesUrl()

        );
    }


    public TournamentPublicDetailData toPublicDetailData(Tournament tournament, long confirmedTeamsCount, boolean isUserRegistered) {
        return new TournamentPublicDetailData(
                tournament.getId(),
                tournament.getName(),
                tournament.getDescription(),
                tournament.getRulesUrl(),
                tournament.getGame(),
                tournament.getStatus(),
                confirmedTeamsCount + "/" + tournament.getMaxTeams(),
                tournament.getPrizePool(),
                tournament.getStartsAt(),
                tournament.getEndsAt(),
                tournament.getImageUrl(),
                isUserRegistered
        );
    }
}
