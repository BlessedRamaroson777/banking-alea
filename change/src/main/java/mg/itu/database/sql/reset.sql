
-- Drop tables if they exist
DROP TABLE IF EXISTS devises_modifications CASCADE;
DROP TABLE IF EXISTS actions_roles CASCADE;
DROP TABLE IF EXISTS devises CASCADE;
DROP TABLE IF EXISTS codes_statuts_devises CASCADE;

-- Recreate tables by including script.sql
\i 'C:\Users\Fetraniaina\OneDrive\Documents\S5\INF301 - Archi log - Mr Tahina\virement_conception\change\src\main\java\mg\itu\database\sql\script.sql'

-- Insert test data for codes_statuts_devises
INSERT INTO codes_statuts_devises (libelle) VALUES ('En attente');
INSERT INTO codes_statuts_devises (libelle) VALUES ('Valide');
INSERT INTO codes_statuts_devises (libelle) VALUES ('Refuse');

-- Insert test data for devises
INSERT INTO devises (code, date_debut, date_fin, cours, statut_code) VALUES ('MGA', '2023-01-01', NULL, 1.00, 1);
INSERT INTO devises (code, date_debut, date_fin, cours, statut_code) VALUES ('USD', '2023-01-01', NULL, 1.5, 1);
INSERT INTO devises (code, date_debut, date_fin, cours, statut_code) VALUES ('EUR', '2023-01-01', NULL, 0.85, 1);
INSERT INTO devises (code, date_debut, date_fin, cours, statut_code) VALUES ('MGA', '2023-01-01', NULL, 4500.00, 1);

-- Insert test data for actions_roles (permissions)
-- Niveau 1 = Lecture seule
-- Niveau 2 = Lecture + Création + Modification
-- Niveau 3 = Tous droits (Admin)

-- Permissions pour la table devises
INSERT INTO actions_roles (nom_table, nom_action, role_minimum) VALUES ('devises', 'READ', 1);
INSERT INTO actions_roles (nom_table, nom_action, role_minimum) VALUES ('devises', 'CREATE', 2);
INSERT INTO actions_roles (nom_table, nom_action, role_minimum) VALUES ('devises', 'UPDATE', 2);
INSERT INTO actions_roles (nom_table, nom_action, role_minimum) VALUES ('devises', 'DELETE', 3);
INSERT INTO actions_roles (nom_table, nom_action, role_minimum) VALUES ('devises', 'VALIDATE', 3);

-- Permissions pour la table devises_modifications
INSERT INTO actions_roles (nom_table, nom_action, role_minimum) VALUES ('devises_modifications', 'READ', 1);
INSERT INTO actions_roles (nom_table, nom_action, role_minimum) VALUES ('devises_modifications', 'CREATE', 2);
INSERT INTO actions_roles (nom_table, nom_action, role_minimum) VALUES ('devises_modifications', 'UPDATE', 3);
INSERT INTO actions_roles (nom_table, nom_action, role_minimum) VALUES ('devises_modifications', 'DELETE', 3);
INSERT INTO actions_roles (nom_table, nom_action, role_minimum) VALUES ('devises_modifications', 'VALIDATE', 3);