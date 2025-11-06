
CREATE TABLE codes_statuts_devises
(
    id      SERIAL PRIMARY KEY,
    libelle VARCHAR(20) NOT NULL
);

CREATE TABLE devises
(
    id                SERIAL PRIMARY KEY,
    code              VARCHAR(3)     NOT NULL,
    date_debut        DATE           NOT NULL,
    date_fin          DATE,
    cours             DECIMAL(15, 2) NOT NULL DEFAULT 0,
    statut_code       INTEGER REFERENCES codes_statuts_devises (id),
    date_modification DATE
);

CREATE TABLE actions_roles
(
    id           SERIAL PRIMARY KEY,
    nom_table    VARCHAR(100) NOT NULL,
    nom_action   VARCHAR(100) NOT NULL,
    role_minimum INTEGER      NOT NULL DEFAULT 1
);

-- Table intermédiaire pour les modifications de devises en attente de validation
CREATE TABLE devises_modifications
(
    id                SERIAL PRIMARY KEY,
    devise_id         INTEGER        NOT NULL REFERENCES devises (id) ON DELETE CASCADE,
    code              VARCHAR(3),
    date_debut        DATE,
    date_fin          DATE,
    cours             DECIMAL(15, 2),
    statut_code       INTEGER REFERENCES codes_statuts_devises (id),
    statut_validation INTEGER        NOT NULL DEFAULT 1 REFERENCES codes_statuts_devises (id),
    date_proposition  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_traitement   TIMESTAMP,
    CONSTRAINT check_au_moins_un_champ CHECK (
        code IS NOT NULL OR
        date_debut IS NOT NULL OR
        date_fin IS NOT NULL OR
        cours IS NOT NULL OR
        statut_code IS NOT NULL
    )
);
