CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- admin: admin123
-- user: user123
insert into usuario
(id, nome, senha, email, roles)
values
( uuid_generate_v4(), 'admin', '$2a$12$6KscFSLl7HSzCqHFE2lrAu8eop0AW2AnTvI4nnKe1OZAuw4jC7ds.', 'admin@gmail.com', '{ADMIN}' ),
( uuid_generate_v4(), 'user', '$2a$12$9MrQw.Y8x4STgx5v/JPqbOpiPprDVwprFgxWW.Ntj0hbngDcjX9Cm', 'user@gmail.com', '{OPERADOR}' );