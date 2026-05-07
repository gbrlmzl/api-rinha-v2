package rinhacampusiv.api.v2.controller.tournaments.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentGame;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.TournamentStatus;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentAdminDetailData;
import rinhacampusiv.api.v2.domain.tournaments.tournaments.dtos.admin.TournamentCreationData;
import rinhacampusiv.api.v2.domain.user.UserRepository;
import rinhacampusiv.api.v2.infra.exception.tournaments.TournamentNotFoundException;
import rinhacampusiv.api.v2.infra.exception.tournaments.ValidatorException;
import rinhacampusiv.api.v2.service.authentication.TokenService;
import rinhacampusiv.api.v2.service.tournaments.admin.AdminTournamentService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Slice limitada ao controller — security desabilitada porque o foco aqui é
// validação de input, status codes e serialização. Auth/role é testada à parte.
@WebMvcTest(AdminTournamentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminTournamentControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private AdminTournamentService adminTournamentService;

    // Dependências do SecurityFilter — precisam existir no contexto mesmo com filtros desabilitados
    @MockitoBean private TokenService tokenService;
    @MockitoBean private UserRepository userRepository;

    // ObjectMapper local com suporte a OffsetDateTime — não dependemos do bean da app aqui
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private MockMultipartFile imagePart() {
        return new MockMultipartFile(
                "image", "torneio.png", MediaType.IMAGE_PNG_VALUE, new byte[]{1, 2, 3});
    }

    private MockMultipartFile dataPart(Object payload) throws Exception {
        return new MockMultipartFile(
                "data", "data.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(payload));
    }

    private TournamentCreationData validPayload() {
        return new TournamentCreationData(
                "Torneio Inter Cursos",
                TournamentGame.LEAGUE_OF_LEGENDS,
                16,
                new BigDecimal("1000.00"),
                OffsetDateTime.now().plusDays(7),
                OffsetDateTime.now().plusDays(8),
                "Descrição do torneio de teste",
                "https://docs.google.com/regras"
        );
    }

    @Test
    void deveCriarTorneioERetornar201ComLocation() throws Exception {
        TournamentAdminDetailData response = new TournamentAdminDetailData(
                42L, "torneio-inter-cursos", "Torneio Inter Cursos",
                TournamentGame.LEAGUE_OF_LEGENDS, TournamentStatus.OPEN,
                16, new BigDecimal("1000.00"),
                OffsetDateTime.now().plusDays(7), OffsetDateTime.now().plusDays(8),
                OffsetDateTime.now(), "desc", "https://i.imgur.com/abc.png",
                "https://docs.google.com/regras"
        );
        when(adminTournamentService.createTournament(any(), any())).thenReturn(response);

        mockMvc.perform(multipart("/admin/tournaments")
                        .file(dataPart(validPayload()))
                        .file(imagePart()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void deveRetornar400QuandoNomeMenorQueMinimo() throws Exception {
        // Nome com 5 chars viola @Size(min=10)
        var invalido = new TournamentCreationData(
                "curto",
                TournamentGame.LEAGUE_OF_LEGENDS,
                16,
                new BigDecimal("1000.00"),
                OffsetDateTime.now().plusDays(7),
                OffsetDateTime.now().plusDays(8),
                "Descrição",
                "https://docs.google.com/regras"
        );

        mockMvc.perform(multipart("/admin/tournaments")
                        .file(dataPart(invalido))
                        .file(imagePart()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoMaxTeamsAbaixoDoMinimo() throws Exception {
        // maxTeams=1 viola @Min(2)
        var invalido = new TournamentCreationData(
                "Torneio Inter Cursos",
                TournamentGame.LEAGUE_OF_LEGENDS,
                1,
                new BigDecimal("1000.00"),
                OffsetDateTime.now().plusDays(7),
                OffsetDateTime.now().plusDays(8),
                "Descrição",
                "https://docs.google.com/regras"
        );

        mockMvc.perform(multipart("/admin/tournaments")
                        .file(dataPart(invalido))
                        .file(imagePart()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar204AoCancelarTorneio() throws Exception {
        mockMvc.perform(patch("/admin/tournaments/{id}", 1L).param("force", "true"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar422QuandoServiceLancaValidatorExceptionNoCancelamento() throws Exception {
        // Cenário: torneio já cancelado / equipes vinculadas sem force — service lança ValidatorException,
        // GlobalExceptionHandler converte em 422 Unprocessable Content
        doThrow(new ValidatorException("Este torneio já está cancelado."))
                .when(adminTournamentService).cancelTournament(anyLong(), anyBoolean());

        mockMvc.perform(patch("/admin/tournaments/{id}", 1L))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Este torneio já está cancelado."));
    }

    @Test
    void deveRetornar404QuandoTorneioInexistente() throws Exception {
        when(adminTournamentService.getTournamentById(999L))
                .thenThrow(new TournamentNotFoundException("Torneio não encontrado"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/admin/tournaments/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
