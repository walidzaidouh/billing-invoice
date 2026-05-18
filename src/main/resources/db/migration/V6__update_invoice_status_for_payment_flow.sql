-- V6__update_invoice_status_for_payment_flow.sql
-- Ajoute les nouveaux statuts du flux event-driven avec le Payment Service.
-- DRAFT     : facture créée, demande envoyée au Payment Service
-- PROCESSING: Payment Service a bien reçu et traite
-- PAID      : paiement accepté (remplace PAYEE pour alignement avec le contrat)
-- REJECTED  : paiement refusé

-- PostgreSQL : l'ALTER TYPE ADD VALUE ne supporte pas les transactions,
-- donc ces instructions doivent être exécutées en dehors d'un bloc transactionnel.
-- Flyway les exécute en mode non-transactionnel si configuré, sinon les
-- mettre dans un script séparé marqué runInTransaction=false.

-- Si le type status_invoice est un CHECK CONSTRAINT (colonne varchar), utilisez
-- simplement les nouveaux statuts directement — aucune migration SQL requise.
-- Si c'est un type ENUM PostgreSQL natif, décommentez les lignes ci-dessous :

-- ALTER TYPE invoice.status_invoice ADD VALUE IF NOT EXISTS 'DRAFT';
-- ALTER TYPE invoice.status_invoice ADD VALUE IF NOT EXISTS 'PROCESSING';
-- ALTER TYPE invoice.status_invoice ADD VALUE IF NOT EXISTS 'PAID';
-- ALTER TYPE invoice.status_invoice ADD VALUE IF NOT EXISTS 'REJECTED';

-- Migration des données existantes (facultatif selon votre contexte) :
-- UPDATE invoice.invoice SET status = 'DRAFT'  WHERE status = 'EN_ATTENTE';
-- UPDATE invoice.invoice SET status = 'PAID'   WHERE status = 'PAYEE';

-- Colonne payment_rejection_reason pour tracer le motif de refus Payment
ALTER TABLE invoice.invoice
    ADD COLUMN IF NOT EXISTS payment_rejection_reason VARCHAR(500);