-- === DONNEES DE TEST POUR MODULE COURANT === --

-- Utilisateurs
INSERT INTO utilisateurs (nom, mot_de_passe, niveau)
VALUES ('alice', 'secret', 1),
       ('bob', 'password', 2),
       ('charlie', 'admin', 3);

-- Actions roles (permissions)
INSERT INTO actions_roles (nom_table, nom_action, role_minimum)
VALUES ('Virement', 'CREER', 1),
       ('Virement', 'VALIDER', 2),
       ('Transaction', 'CREER', 1);

-- Types de comptes
INSERT INTO types_comptes (libelle)
VALUES ('Courant'),
       ('Epargne');

-- Comptes
INSERT INTO comptes (numero, type_id, plafond, solde)
VALUES ('123456789', 1, 10000.00, 5000.00), -- Compte d'Alice
       ('987654321', 1, 5000.00, 2000.00),  -- Compte de Bob
       ('555666777', 2, 0.00, 10000.00);
-- Compte épargne

-- Config frais (pour type compte 1 - Courant)
INSERT INTO config_frais (type_compte_id, min, max, frais_montant, frais_pourcentage)
VALUES (1, 0, 1000, 5.00, 0.00),    -- Frais fixes de 5€ pour montants <= 1000€
       (1, 1000, 5000, 0.00, 0.01), -- 1% pour montants entre 1000€ et 5000€
       (1, 5000, 999999, 0.00, 0.02), -- 2% pour montants > 5000€
       -- Config frais pour type compte 2 - Epargne
       (2, 0, 1000, 5.00, 0.00),    -- Frais fixes de 5€ pour montants <= 1000€
       (2, 1000, 5000, 0.00, 0.01), -- 1% pour montants entre 1000€ et 5000€
       (2, 5000, 999999, 0.00, 0.02); -- 2% pour montants > 5000€

-- Codes types transactions
INSERT INTO codes_types_transactions (libelle)
VALUES ('DEBIT'),
       ('CREDIT'),
       ('FRAIS');

-- Virement existant pour tester validation (statut 1 = en attente)
INSERT INTO virements (montant, date_creation, date_effet, compte_envoyeur, compte_destinataire, utilisateur_id,
                       statut_code, change_id)
VALUES (500.00, NOW(), NOW() + INTERVAL '1 day', 1, 2, 1, 1, 1);

-- Transactions associées au virement (statut 1 = en attente)
INSERT INTO transactions (compte_id, virement_id, type_code, date_creation, montant, statut_code, utilisateur_id)
VALUES (1, 1, 1, NOW(), 500.00, 1, 1), -- Débit du compte envoyeur
       (2, 1, 2, NOW(), 495.00, 1, 1), -- Crédit du destinataire (montant - frais)
       (1, 1, 1, NOW(), 5.00, 1, 1); -- Frais sur compte envoyeur