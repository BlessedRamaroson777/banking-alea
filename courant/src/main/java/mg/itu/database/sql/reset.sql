-- === RESET SCRIPT FOR MODULE COURANT === --
-- This script truncates all tables in the courant database to reset the data.
-- Note: This assumes no foreign key constraints prevent truncation.
-- If constraints exist, use CASCADE or truncate in dependency order.

TRUNCATE TABLE validations_transactions CASCADE;
TRUNCATE TABLE validations_virements CASCADE;
TRUNCATE TABLE transactions CASCADE;
TRUNCATE TABLE virements CASCADE;
TRUNCATE TABLE comptes CASCADE;
TRUNCATE TABLE config_frais CASCADE;
TRUNCATE TABLE actions_roles CASCADE;
TRUNCATE TABLE utilisateurs CASCADE;
TRUNCATE TABLE types_comptes CASCADE;
TRUNCATE TABLE codes_types_transactions CASCADE;
