-- Seed minimal data for local/dev testing
-- Creates a Customer, a Creancier, and two PointDeVente rows (AGENCE + DISTRIBUTEUR)
-- so that POST /api/invoices can be tested immediately.

-- Use high, deterministic IDs to avoid collisions with existing data.

INSERT INTO invoice.customer (id, nom, prenom, cin, email, telephone, adresse, ville, created_date, updated_date)
VALUES (1001, 'Test', 'Client', 'AA123456', 'client@test.com', '0600000000', 'Rue 1', 'Rabat', now(), now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO invoice.creancier (id, nom, type_creancier, ice, rc, rib, banque, email, telephone, adresse, created_date, updated_date)
VALUES (2001, 'ONEE', 'ONEE', NULL, NULL, NULL, NULL, 'onee@test.com', NULL, NULL, now(), now())
ON CONFLICT (id) DO NOTHING;

-- AGENCE
INSERT INTO invoice.point_de_vente (
    id,
    type_point_de_vente,
    nom,
    adresse,
    telephone,
    code_agence,
    responsable,
    region,
    type_agence,
    created_date,
    updated_date
)
VALUES (
    3001,
    'AGENCE',
    'Agence Centre',
    'Centre',
    '0500000000',
    'AG001',
    'Resp 1',
    'Rabat',
    'URBAINE',
    now(),
    now()
)
ON CONFLICT (id) DO NOTHING;

-- DISTRIBUTEUR
INSERT INTO invoice.point_de_vente (
    id,
    type_point_de_vente,
    nom,
    adresse,
    telephone,
    code_distributeur,
    zone_distribution,
    nom_commercial,
    commission,
    created_date,
    updated_date
)
VALUES (
    3002,
    'DISTRIBUTEUR',
    'Distributeur Nord',
    'Nord',
    '0500000001',
    'DIS001',
    'Zone Nord',
    'Shop Nord',
    10.0,
    now(),
    now()
)
ON CONFLICT (id) DO NOTHING;

-- Ensure the shared sequence is ahead of our seeded IDs to avoid duplicate key errors later.
DO $$
BEGIN
    BEGIN
        PERFORM setval('invoice.global_sequence', 5000, true);
    EXCEPTION
        WHEN undefined_table THEN
            PERFORM setval('global_sequence', 5000, true);
    END;
END $$;
