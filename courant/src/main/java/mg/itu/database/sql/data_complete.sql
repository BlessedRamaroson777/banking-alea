-- === JEU DE DONNEES COMPLET POUR MODULE COURANT === --

-- ============================================
-- DONNEES STATIQUES (Tables de référence)
-- ============================================
-- Types de comptes
INSERT INTO types_comptes (libelle)
VALUES ('Courant'),
       ('Epargne');

-- Codes types transactions
INSERT INTO codes_types_transactions (libelle)
VALUES ('DEBIT'),
       ('CREDIT'),
       ('FRAIS');

-- ============================================
-- UTILISATEURS (5 utilisateurs avec différents niveaux)
-- ============================================
INSERT INTO utilisateurs (nom, mot_de_passe, niveau)
VALUES 
    ('alice', 'password123', 1),      -- Niveau 1 : Utilisateur simple (peut créer)
    ('bob', 'password123', 2),        -- Niveau 2 : Validateur (peut créer et valider)
    ('charlie', 'password123', 3),    -- Niveau 3 : Admin (peut tout faire)
    ('david', 'password123', 1),      -- Niveau 1 : Utilisateur simple
    ('emma', 'password123', 2);       -- Niveau 2 : Validateur

-- ============================================
-- ACTIONS ROLES (Permissions)
-- ============================================
INSERT INTO actions_roles (nom_table, nom_action, role_minimum)
VALUES 
    ('Virement', 'CREER', 1),         -- Niveau 1 minimum pour créer un virement
    ('Virement', 'VALIDER', 2),       -- Niveau 2 minimum pour valider un virement
    ('Transaction', 'CREER', 1),      -- Niveau 1 minimum pour créer une transaction
    ('Transaction', 'VALIDER', 2);    -- Niveau 2 minimum pour valider une transaction

-- ============================================
-- COMPTES (10 comptes variés)
-- ============================================
INSERT INTO comptes (numero, type_id, plafond, solde)
VALUES 
    -- Comptes Courants (type_id = 1)
    ('ACC-001', 1, 10000.00, 5000.00),    -- Compte d'Alice
    ('ACC-002', 1, 15000.00, 8500.00),    -- Compte de Bob
    ('ACC-003', 1, 20000.00, 12000.00),   -- Compte de Charlie
    ('ACC-004', 1, 8000.00, 3200.00),     -- Compte de David
    ('ACC-005', 1, 12000.00, 6800.00),    -- Compte d'Emma
    
    -- Comptes Epargne (type_id = 2)
    ('SAV-001', 2, 0.00, 25000.00),       -- Epargne d'Alice
    ('SAV-002', 2, 0.00, 50000.00),       -- Epargne de Bob
    ('SAV-003', 2, 0.00, 100000.00),      -- Epargne de Charlie
    ('SAV-004', 2, 0.00, 15000.00),       -- Epargne de David
    ('SAV-005', 2, 0.00, 30000.00);       -- Epargne d'Emma

-- ============================================
-- CONFIG FRAIS (Configuration pour tous les types de comptes)
-- ============================================
INSERT INTO config_frais (type_compte_id, min, max, frais_montant, frais_pourcentage)
VALUES 
    -- Frais pour Comptes Courants (type_id = 1)
    (1, 0, 1000, 5.00, 0.00),             -- 0-1000€ : 5€ fixe
    (1, 1000, 5000, 0.00, 0.01),          -- 1000-5000€ : 1%
    (1, 5000, 999999, 0.00, 0.02),        -- >5000€ : 2%
    
    -- Frais pour Comptes Epargne (type_id = 2)
    (2, 0, 1000, 3.00, 0.00),             -- 0-1000€ : 3€ fixe (moins cher)
    (2, 1000, 5000, 0.00, 0.005),         -- 1000-5000€ : 0.5%
    (2, 5000, 999999, 0.00, 0.01);        -- >5000€ : 1%

-- ============================================
-- VIREMENTS (Plusieurs virements avec différents statuts)
-- ============================================
INSERT INTO virements (montant, date_creation, date_effet, compte_envoyeur, compte_destinataire, utilisateur_id, statut_code, change_id)
VALUES 
    -- Virements EN ATTENTE (statut_code = 1)
    (500.00, NOW() - INTERVAL '2 hours', NOW() + INTERVAL '1 day', 1, 2, 1, 1, 1),
    (1200.00, NOW() - INTERVAL '1 hour', NOW() + INTERVAL '2 days', 4, 5, 4, 1, 1),
    (300.00, NOW() - INTERVAL '30 minutes', NOW() + INTERVAL '1 day', 6, 7, 1, 1, 1),
    
    -- Virements VALIDES (statut_code = 11)
    (750.00, NOW() - INTERVAL '3 days', NOW() - INTERVAL '2 days', 2, 3, 2, 11, 1),
    (2000.00, NOW() - INTERVAL '5 days', NOW() - INTERVAL '4 days', 7, 8, 2, 11, 1),
    
    -- Virements REFUSES (statut_code = -11)
    (10000.00, NOW() - INTERVAL '1 day', NOW(), 1, 3, 1, -11, 1),
    (500000.00, NOW() - INTERVAL '2 days', NOW() - INTERVAL '1 day', 4, 5, 4, -11, 1);

-- ============================================
-- TRANSACTIONS (Pour les virements validés et en attente)
-- ============================================
-- Transactions pour virement 1 (500€, statut 1 - en attente)
INSERT INTO transactions (compte_id, virement_id, type_code, date_creation, montant, statut_code, utilisateur_id)
VALUES 
    (1, 1, 1, NOW() - INTERVAL '2 hours', 500.00, 1, 1),   -- Débit compte envoyeur
    (2, 1, 2, NOW() - INTERVAL '2 hours', 495.00, 1, 1),   -- Crédit destinataire (500 - 5€ frais)
    (1, 1, 3, NOW() - INTERVAL '2 hours', 5.00, 1, 1);     -- Frais

-- Transactions pour virement 2 (1200€, statut 1 - en attente)
INSERT INTO transactions (compte_id, virement_id, type_code, date_creation, montant, statut_code, utilisateur_id)
VALUES 
    (4, 2, 1, NOW() - INTERVAL '1 hour', 1200.00, 1, 4),   -- Débit
    (5, 2, 2, NOW() - INTERVAL '1 hour', 1188.00, 1, 4),   -- Crédit (1200 - 12€ frais = 1% de 1200)
    (4, 2, 3, NOW() - INTERVAL '1 hour', 12.00, 1, 4);     -- Frais

-- Transactions pour virement 3 (300€, statut 1 - en attente)
INSERT INTO transactions (compte_id, virement_id, type_code, date_creation, montant, statut_code, utilisateur_id)
VALUES 
    (6, 3, 1, NOW() - INTERVAL '30 minutes', 300.00, 1, 1),  -- Débit
    (7, 3, 2, NOW() - INTERVAL '30 minutes', 297.00, 1, 1),  -- Crédit (300 - 3€ frais)
    (6, 3, 3, NOW() - INTERVAL '30 minutes', 3.00, 1, 1);    -- Frais

-- Transactions pour virement 4 (750€, statut 11 - validé)
INSERT INTO transactions (compte_id, virement_id, type_code, date_creation, montant, statut_code, utilisateur_id)
VALUES 
    (2, 4, 1, NOW() - INTERVAL '3 days', 750.00, 11, 2),   -- Débit
    (3, 4, 2, NOW() - INTERVAL '3 days', 745.00, 11, 2),   -- Crédit (750 - 5€)
    (2, 4, 3, NOW() - INTERVAL '3 days', 5.00, 11, 2);     -- Frais

-- Transactions pour virement 5 (2000€, statut 11 - validé)
INSERT INTO transactions (compte_id, virement_id, type_code, date_creation, montant, statut_code, utilisateur_id)
VALUES 
    (7, 5, 1, NOW() - INTERVAL '5 days', 2000.00, 11, 2),  -- Débit
    (8, 5, 2, NOW() - INTERVAL '5 days', 1990.00, 11, 2),  -- Crédit (2000 - 10€ = 0.5% de 2000)
    (7, 5, 3, NOW() - INTERVAL '5 days', 10.00, 11, 2);    -- Frais

-- ============================================
-- VALIDATIONS VIREMENTS (Pour les virements validés et refusés)
-- ============================================
INSERT INTO validations_virements (virement_id, date_validation, utilisateur_id, nouveau_statut_code)
VALUES 
    -- Validations (statut 11)
    (4, NOW() - INTERVAL '2 days 23 hours', 2, 11),
    (5, NOW() - INTERVAL '4 days 23 hours', 3, 11),
    
    -- Refus (statut -11)
    (6, NOW() - INTERVAL '23 hours', 3, -11),
    (7, NOW() - INTERVAL '1 day 23 hours', 2, -11);

-- ============================================
-- RESUME DES DONNEES
-- ============================================
-- Utilisateurs : 5 (3 niveaux différents)
-- Comptes : 10 (5 Courants, 5 Epargne)
-- Config frais : 6 configurations (3 par type de compte)
-- Virements : 7 (3 en attente, 2 validés, 2 refusés)
-- Transactions : 18 (3 par virement)
-- Validations : 4 (2 validations, 2 refus)
