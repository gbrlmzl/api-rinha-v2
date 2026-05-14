-- Adiciona coluna slug na tabela de torneios
-- O preenchimento dos slugs existentes é feito pelo TournamentSlugInitializer no startup
ALTER TABLE tournaments ADD COLUMN IF NOT EXISTS slug VARCHAR(255);

CREATE UNIQUE INDEX IF NOT EXISTS idx_tournaments_slug ON tournaments(slug) WHERE slug IS NOT NULL;
