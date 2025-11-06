-- Script de test pour le système de validation des modifications de devises

-- 1. Créer une devise (sera en attente automatiquement)
INSERT INTO devises (code, date_debut, cours, statut_code) 
VALUES ('GBP', '2024-01-01', 0.85, 1);

-- 2. Valider cette devise pour pouvoir la modifier
UPDATE devises SET statut_code = 2 WHERE code = 'GBP';

-- 3. Proposer une modification du cours
INSERT INTO devises_modifications (devise_id, cours, statut_validation)
VALUES (
    (SELECT id FROM devises WHERE code = 'GBP'),
    0.87,
    1
);

-- 4. Consulter les modifications en attente
SELECT 
    dm.id,
    dm.devise_id,
    d.code as devise_code,
    dm.code as nouveau_code,
    d.cours as cours_actuel,
    dm.cours as nouveau_cours,
    dm.statut_validation,
    cs.libelle as statut
FROM devises_modifications dm
JOIN devises d ON dm.devise_id = d.id
JOIN codes_statuts_devises cs ON dm.statut_validation = cs.id
WHERE dm.statut_validation = 1;

-- 5. Valider une modification (simulation - en réalité via API)
-- UPDATE devises SET cours = 0.87 WHERE code = 'GBP';
-- UPDATE devises_modifications SET statut_validation = 2, date_traitement = CURRENT_TIMESTAMP WHERE id = 1;

-- 6. Consulter l'historique des modifications d'une devise
SELECT 
    dm.id,
    dm.code as nouveau_code,
    dm.cours as nouveau_cours,
    dm.statut_validation,
    cs.libelle as statut,
    dm.date_proposition,
    dm.date_traitement
FROM devises_modifications dm
JOIN codes_statuts_devises cs ON dm.statut_validation = cs.id
WHERE dm.devise_id = (SELECT id FROM devises WHERE code = 'GBP')
ORDER BY dm.date_proposition DESC;

-- 7. Statistiques des modifications
SELECT 
    cs.libelle as statut,
    COUNT(*) as nombre
FROM devises_modifications dm
JOIN codes_statuts_devises cs ON dm.statut_validation = cs.id
GROUP BY cs.libelle;
