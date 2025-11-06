-- Migration: Ajout de la colonne date_modification dans la table devises
-- Date: 2025-11-05

ALTER TABLE devises 
ADD COLUMN date_modification DATE;

-- Vérification
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'devises' 
ORDER BY ordinal_position;
