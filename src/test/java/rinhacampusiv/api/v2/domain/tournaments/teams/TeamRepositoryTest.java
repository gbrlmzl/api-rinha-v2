package rinhacampusiv.api.v2.domain.tournaments.teams;

import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TeamRepositoryTest {

    @Test
    void findByNameAndTournamentId() {
    }

    @Test
    void existsByNameIgnoreCaseAndTournamentId() {
    }

    @Test
    void findByIdWithPayments() {
    }

    @Test
    void findAllByTournamentIdWithDetails() {
    }
}