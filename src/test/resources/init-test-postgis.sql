-- Enable PostGIS extension for geography data type (matching production)
CREATE EXTENSION IF NOT EXISTS postgis;

-- Create the schema without quotes (matching Hibernate expectations)
CREATE SCHEMA IF NOT EXISTS bang9;

-- Set search path for convenience in tests
SET search_path TO bang9, public;