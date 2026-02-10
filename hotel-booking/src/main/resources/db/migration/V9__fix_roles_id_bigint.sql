-- Align roles.id and users.role_id with Hibernate Long/BIGINT expectations.
-- Safe for existing data: converts types and restores FK + sequence default.

ALTER TABLE users DROP CONSTRAINT IF EXISTS fk_user_role;

ALTER TABLE roles
    ALTER COLUMN id TYPE BIGINT;

ALTER SEQUENCE IF EXISTS roles_id_seq AS BIGINT;
ALTER SEQUENCE IF EXISTS roles_id_seq OWNED BY roles.id;
ALTER TABLE roles ALTER COLUMN id SET DEFAULT nextval('roles_id_seq');

ALTER TABLE users
    ALTER COLUMN role_id TYPE BIGINT;

ALTER TABLE users
    ADD CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE;
