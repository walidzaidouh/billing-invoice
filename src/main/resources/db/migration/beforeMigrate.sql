CREATE SCHEMA IF NOT EXISTS invoice;

-- V1 depends on the shared sequence from the very first CREATE TABLE.
-- This callback guarantees the sequence exists before Flyway starts versioned migrations.
-- Fix test database startup by creating invoice.global_sequence before Flyway migrations without using test-only migrations
CREATE SEQUENCE IF NOT EXISTS invoice.global_sequence START WITH 1000 INCREMENT BY 1;
