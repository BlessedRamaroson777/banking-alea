-- === MODULE COURANT === --
\c banque_courant;

CREATE TABLE utilisateurs
(
    id           SERIAL PRIMARY KEY,
    nom          VARCHAR(100) NOT NULL,
    mot_de_passe VARCHAR(100) NOT NULL,
    niveau       INTEGER      NOT NULL DEFAULT 1
);

CREATE TABLE actions_roles
(
    id           SERIAL PRIMARY KEY,
    nom_table    VARCHAR(100),
    nom_action   VARCHAR(100),
    role_minimum INTEGER
);

CREATE TABLE types_comptes
(
    id      SERIAL PRIMARY KEY,
    libelle VARCHAR(100)
);

CREATE TABLE comptes
(
    id      SERIAL PRIMARY KEY,
    numero  VARCHAR(50)    NOT NULL,
    type_id INTEGER REFERENCES types_comptes (id),
    plafond DECIMAL(15, 2) NOT NULL DEFAULT 0,
    solde   DECIMAL(15, 2) DEFAULT 0
);

CREATE TABLE config_frais
(
    id                SERIAL PRIMARY KEY,
    type_compte_id    INTEGER REFERENCES types_comptes (id),
    min               DECIMAL(15, 2) NOT NULL DEFAULT 0,
    max               DECIMAL(15, 2) NOT NULL DEFAULT 0,
    frais_montant     DECIMAL(15, 2)          DEFAULT 0,
    frais_pourcentage DECIMAL(5, 2)           DEFAULT 0
);

CREATE TABLE virements
(
    id                  SERIAL PRIMARY KEY,
    montant             DECIMAL(15, 2),
    date_creation       TIMESTAMP DEFAULT NOW(),
    date_effet          TIMESTAMP DEFAULT NOW(),
    compte_envoyeur     INTEGER REFERENCES comptes (id),
    compte_destinataire INTEGER REFERENCES comptes (id),
    utilisateur_id      INTEGER REFERENCES utilisateurs (id) NOT NULL,
    statut_code         INTEGER   DEFAULT 1                  NOT NULL,
    change_id           INTEGER                              NOT NULL
);

CREATE TABLE codes_types_transactions
(
    id      SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL-- débit, crédit, frais
);

CREATE TABLE transactions
(
    id             SERIAL PRIMARY KEY,
    compte_id      INTEGER REFERENCES comptes (id),
    virement_id    INTEGER REFERENCES virements (id)                NULL,
    type_code      INTEGER REFERENCES codes_types_transactions (id) NOT NULL,
    date_creation  TIMESTAMP                                                 DEFAULT NOW(),
    montant        DECIMAL(15, 2)                                   NOT NULL DEFAULT 0,
    statut_code    INTEGER                                                   DEFAULT 1,
    utilisateur_id INTEGER REFERENCES utilisateurs (id)
);

CREATE TABLE validations_transactions
(
    id                  SERIAL PRIMARY KEY,
    transaction_id      INTEGER REFERENCES transactions (id) NOT NULL,
    date_validation     TIMESTAMP DEFAULT NOW()              NOT NULL,
    utilisateur_id      INTEGER REFERENCES utilisateurs (id) NOT NULL,
    nouveau_statut_code INTEGER   DEFAULT 1
);

CREATE TABLE validations_virements
(
    id                  SERIAL PRIMARY KEY,
    virement_id         INTEGER REFERENCES virements (id)    NOT NULL,
    date_validation     TIMESTAMP DEFAULT NOW()              NOT NULL,
    utilisateur_id      INTEGER REFERENCES utilisateurs (id) NOT NULL,
    nouveau_statut_code INTEGER   DEFAULT 1
);
