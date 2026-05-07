package rinhacampusiv.api.v2.service.tournaments.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import rinhacampusiv.api.v2.domain.tournaments.teams.Team;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamRepository;
import rinhacampusiv.api.v2.domain.tournaments.teams.TeamStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.Tournament;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentRepository;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentAdminDetailData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentAdminSummaryData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentCreationData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentUpdateData;
import rinhacampusiv.api.v2.infra.exception.tournaments.ValidatorException;
import rinhacampusiv.api.v2.infra.external.imgur.ImgurClient;
import rinhacampusiv.api.v2.service.tournaments.payment.PaymentCancellationService;
import rinhacampusiv.api.v2.validators.tournament.creation.TournamentCreationValidator;
import rinhacampusiv.api.v2.validators.tournament.update.TournamentUpdateValidator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTournamentServiceTest {

    @Mock private TournamentRepository tournamentRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private ImgurClient imgurClient;
    @Mock private PaymentCancellationService paymentCancellationService;
    @Mock private TournamentCreationValidator creationValidator;
    @Mock private TournamentUpdateValidator updateValidator;
    @Mock private MultipartFile image;

    @InjectMocks private AdminTournamentService service;

    @BeforeEach
    void setUp() {
        // Listas de validators são injetadas via @Autowired List<...> — Mockito não preenche sozinho
        ReflectionTestUtils.setField(service, "creationValidators", List.of(creationValidator));
        ReflectionTestUtils.setField(service, "updateValidators", List.of(updateValidator));
    }

    private TournamentCreationData validCreationData() {
        return new TournamentCreationData(
                "Torneio Inter Cursos",
                TournamentGame.LEAGUE_OF_LEGENDS,
                16,
                new BigDecimal("1000.00"),
                OffsetDateTime.now().plusDays(7),
                OffsetDateTime.now().plusDays(8),
                "Descrição do torneio",
                "https://docs.google.com/regras"
        );
    }

    private Tournament tournamentWithStatus(Long id, TournamentStatus status, int maxTeams) {
        Tournament t = new Tournament();
        t.setId(id);
        t.setName("Torneio X");
        t.setGame(TournamentGame.LEAGUE_OF_LEGENDS);
        t.setStatus(status);
        t.setMaxTeams(maxTeams);
        return t;
    }

    @Nested
    @DisplayName("createTournament")
    class CreateTournament {

        @Test
        @DisplayName("salva torneio com URL retornada pelo Imgur e devolve detail data")
        void deveCriarTorneioComImageUrlDoImgur() {
            var data = validCreationData();
            when(imgurClient.uploadTournamentImage(image, data.name())).thenReturn("https://i.imgur.com/abc.png");

            TournamentAdminDetailData response = service.createTournament(data, image);

            verify(imgurClient).validateImage(image);
            verify(creationValidator).validar(data);

            // Captura o Tournament passado ao save() e checa o estado pós-construção
            ArgumentCaptor<Tournament> captor = ArgumentCaptor.forClass(Tournament.class);
            verify(tournamentRepository).save(captor.capture());

            Tournament saved = captor.getValue();
            assertThat(saved.getName()).isEqualTo(data.name());
            assertThat(saved.getStatus()).isEqualTo(TournamentStatus.OPEN);
            assertThat(saved.getImageUrl()).isEqualTo("https://i.imgur.com/abc.png");
            assertThat(response.name()).isEqualTo(data.name());
        }

        @Test
        @DisplayName("propaga erro do validator e não chega a fazer upload nem persistir")
        void deveAbortarQuandoValidatorFalha() {
            var data = validCreationData();
            doThrow(new ValidatorException("Torneio já existe")).when(creationValidator).validar(data);

            assertThatThrownBy(() -> service.createTournament(data, image))
                    .isInstanceOf(ValidatorException.class)
                    .hasMessage("Torneio já existe");

            // validateImage roda antes do validar(); upload e save NÃO devem ser chamados
            verify(imgurClient).validateImage(image);
            verify(imgurClient, never()).uploadTournamentImage(any(), any());
            verify(tournamentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getAllTournaments")
    class ListTournaments {

        @Test
        @DisplayName("monta os 3 contadores de equipes (confirmadas, ativas, total) por torneio")
        void deveMontarSummaryComContadoresDeEquipes() {
            Pageable pageable = PageRequest.of(0, 10);
            Tournament t1 = tournamentWithStatus(1L, TournamentStatus.OPEN, 16);
            Page<Tournament> page = new PageImpl<>(List.of(t1), pageable, 1);

            when(tournamentRepository.findAll(pageable)).thenReturn(page);
            when(teamRepository.countByTournamentIdsAndStatus(List.of(1L), TeamStatus.READY))
                    .thenReturn(List.<Object[]>of(new Object[]{1L, 5L}));
            when(teamRepository.countByTournamentIdsAndStatusIn(eq(List.of(1L)), anyList()))
                    .thenReturn(List.<Object[]>of(new Object[]{1L, 8L}));
            when(teamRepository.countByTournamentIds(List.of(1L)))
                    .thenReturn(List.<Object[]>of(new Object[]{1L, 10L}));

            Page<TournamentAdminSummaryData> result = service.getAllTournaments(null, pageable);

            assertThat(result.getContent()).hasSize(1);
            TournamentAdminSummaryData summary = result.getContent().getFirst();
            assertThat(summary.confirmedTeamsCount()).isEqualTo(5);
            assertThat(summary.activeTeamsCount()).isEqualTo(8);
            assertThat(summary.totalTeamsCount()).isEqualTo(10);
        }

        @Test
        @DisplayName("usa findByGame quando filtro de jogo é informado")
        void deveFiltrarPorJogoQuandoInformado() {
            Pageable pageable = PageRequest.of(0, 10);
            when(tournamentRepository.findByGame(TournamentGame.VALORANT, pageable))
                    .thenReturn(new PageImpl<>(List.of(), pageable, 0));
            when(teamRepository.countByTournamentIdsAndStatus(anyList(), any())).thenReturn(List.of());
            when(teamRepository.countByTournamentIdsAndStatusIn(anyList(), anyList())).thenReturn(List.of());
            when(teamRepository.countByTournamentIds(anyList())).thenReturn(List.of());

            service.getAllTournaments(TournamentGame.VALORANT, pageable);

            verify(tournamentRepository).findByGame(TournamentGame.VALORANT, pageable);
            verify(tournamentRepository, never()).findAll(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("updateTournament")
    class UpdateTournament {

        @Test
        @DisplayName("transiciona OPEN→FULL quando maxTeams iguala o número de equipes ativas")
        void deveAtualizarParaFullQuandoLotar() {
            Tournament tournament = tournamentWithStatus(1L, TournamentStatus.OPEN, 32);
            TournamentUpdateData data = new TournamentUpdateData(
                    null, null, 16, null, null, null, null, null, null, null);

            when(tournamentRepository.findByIdOrThrow(1L)).thenReturn(tournament);
            when(teamRepository.countByActiveTrueAndTournamentId(1L)).thenReturn(16);

            service.updateTournament(1L, data, null);

            assertThat(tournament.getStatus()).isEqualTo(TournamentStatus.FULL);
            assertThat(tournament.getMaxTeams()).isEqualTo(16);
        }

        @Test
        @DisplayName("transiciona FULL→OPEN quando admin aumenta maxTeams acima do total atual")
        void deveAtualizarParaOpenQuandoAumentaLimite() {
            Tournament tournament = tournamentWithStatus(1L, TournamentStatus.FULL, 16);
            TournamentUpdateData data = new TournamentUpdateData(
                    null, null, 32, null, null, null, null, null, null, null);

            when(tournamentRepository.findByIdOrThrow(1L)).thenReturn(tournament);
            when(teamRepository.countByActiveTrueAndTournamentId(1L)).thenReturn(16);

            service.updateTournament(1L, data, null);

            assertThat(tournament.getStatus()).isEqualTo(TournamentStatus.OPEN);
        }

        @Test
        @DisplayName("não chama Imgur quando imagem não é enviada na atualização")
        void naoDevefazerUploadQuandoImagemNula() {
            Tournament tournament = tournamentWithStatus(1L, TournamentStatus.OPEN, 16);
            TournamentUpdateData data = new TournamentUpdateData(
                    "Novo Nome do Torneio", null, null, null, null, null,
                    "nova descrição", null, null, null);

            when(tournamentRepository.findByIdOrThrow(1L)).thenReturn(tournament);

            service.updateTournament(1L, data, null);

            verifyNoInteractions(imgurClient);
            assertThat(tournament.getName()).isEqualTo("Novo Nome do Torneio");
            assertThat(tournament.getDescription()).isEqualTo("nova descrição");
        }
    }

    @Nested
    @DisplayName("cancelTournament")
    class CancelTournament {

        @Test
        @DisplayName("rejeita cancelar torneio que já está CANCELED")
        void deveRejeitarSeJaCancelado() {
            Tournament tournament = tournamentWithStatus(1L, TournamentStatus.CANCELED, 16);
            when(tournamentRepository.findByIdOrThrow(1L)).thenReturn(tournament);

            assertThatThrownBy(() -> service.cancelTournament(1L, false))
                    .isInstanceOf(ValidatorException.class)
                    .hasMessageContaining("já está cancelado");

            verify(tournamentRepository, never()).delete(any());
            verifyNoInteractions(paymentCancellationService);
        }

        @Test
        @DisplayName("deleta o torneio quando não há equipes vinculadas")
        void deveDeletarQuandoNaoHaEquipes() {
            Tournament tournament = tournamentWithStatus(1L, TournamentStatus.OPEN, 16);
            when(tournamentRepository.findByIdOrThrow(1L)).thenReturn(tournament);
            when(teamRepository.countByTournamentId(1L)).thenReturn(0);

            service.cancelTournament(1L, false);

            verify(tournamentRepository).delete(tournament);
            verifyNoInteractions(paymentCancellationService);
        }

        @Test
        @DisplayName("exige flag force=true para cancelar quando há equipes vinculadas")
        void deveExigirForceQuandoHaEquipes() {
            Tournament tournament = tournamentWithStatus(1L, TournamentStatus.OPEN, 16);
            when(tournamentRepository.findByIdOrThrow(1L)).thenReturn(tournament);
            when(teamRepository.countByTournamentId(1L)).thenReturn(4);

            assertThatThrownBy(() -> service.cancelTournament(1L, false))
                    .isInstanceOf(ValidatorException.class)
                    .hasMessageContaining("4 equipe");

            // Confirmação ainda não veio; nada é alterado
            assertThat(tournament.getStatus()).isEqualTo(TournamentStatus.OPEN);
            verify(tournamentRepository, never()).delete(any());
            verifyNoInteractions(paymentCancellationService);
        }

        @Test
        @DisplayName("com force=true cancela pagamentos de cada equipe e marca torneio como CANCELED")
        void deveCancelarPagamentosEMarcarStatusCanceled() {
            Tournament tournament = tournamentWithStatus(1L, TournamentStatus.OPEN, 16);
            Team team1 = new Team();
            team1.setId(10L);
            team1.setActive(true);
            Team team2 = new Team();
            team2.setId(11L);
            team2.setActive(true);

            when(tournamentRepository.findByIdOrThrow(1L)).thenReturn(tournament);
            when(teamRepository.countByTournamentId(1L)).thenReturn(2);
            when(teamRepository.findAllByTournamentIdWithPayments(1L)).thenReturn(List.of(team1, team2));

            service.cancelTournament(1L, true);

            verify(paymentCancellationService, times(2)).cancelTeamPayments(any(Team.class), eq("TORNEIO"));
            assertThat(tournament.getStatus()).isEqualTo(TournamentStatus.CANCELED);
            assertThat(team1.isActive()).isFalse();
            assertThat(team2.isActive()).isFalse();
            assertThat(team1.getStatus()).isEqualTo(TeamStatus.CANCELED);
        }
    }
}
