-- migration_remove_team_constraints.sql
ALTER TABLE teams
    DROP CONSTRAINT uq_captain_tournament,
    DROP CONSTRAINT uq_team_name_tournament;
