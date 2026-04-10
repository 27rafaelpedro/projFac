-- ----------------------------------------------------------------------------
-- SIBD - SISTEMAS DE INFORMAÇÃO E BASES DE DADOS
-- Etapa 2 do Projeto de 2025/2026
-- Grupo 25: Rafael Pedro fc63697, Josué Dias fc63699, Gabriel Brilha fc63792
-- 
-- Contribuições: 
-- Rafael Pedro:   33.33% - Fez a tradução da EA e as RIAs.
-- Josué Dias:     33.33% - Fez a base do Modelo Relacional 
--                          e contribuiu para as RIAs.
-- Gabriel Brilha: 33.33% - Fez os comandos INSERTs, DROPs.
-- ----------------------------------------------------------------------------
-- RIA suportadas: 1, 2, 7, 10, 11, 12, 13
-- Não suportadas: 3, 4, 5, 6, 8, 9, 14, 15, 16, 17, 18
-- ----------------------------------------------------------------------------

-- ----------------------------------------------------------------------------
-- DROPS
-- ----------------------------------------------------------------------------
DROP TABLE membro;
DROP TABLE refere;
DROP TABLE possui;
DROP TABLE lista_personalizada;
DROP TABLE album_suporte_fisico_versao;
DROP TABLE album_suporte_fisico;
DROP TABLE utilizador;
DROP TABLE album;
DROP TABLE artista_grupo;
DROP TABLE artista_solista;
DROP TABLE versao;
DROP TABLE suporte_fisico;
DROP TABLE artista; 
-- ----------------------------------------------------------------------------

-- ----------------------------------------------------------------------------
ALTER SESSION SET NLS_DATE_FORMAT = 'DD.MM.YYYY';
-- ----------------------------------------------------------------------------

-- ----------------------------------------------------------------------------
--
CREATE TABLE artista (
    isni          VARCHAR (16), 
    nome          VARCHAR (40) CONSTRAINT nn_artista_nome          NOT NULL,
    ano_atividade NUMBER  (4)  CONSTRAINT nn_artista_ano_atividade NOT NULL,
--
    CONSTRAINT pk_artista
        PRIMARY KEY (isni),
--
    CONSTRAINT ck_artista_ano_atividade
        CHECK (ano_atividade > 0),
--  
    CONSTRAINT ck_artista_isni -- RIA-07: O código ISNI de um artista tem 
		               -- de ter 16 dígitos
        CHECK (LENGTH (isni) = 16)    
);
--
-- ----------------------------------------------------------------------------
--
CREATE TABLE artista_solista ( -- RIA-10: Solista AND Grupo COVER Artista.
    artista,
--
    CONSTRAINT pk_artista_solista
        PRIMARY KEY (artista),
--
    CONSTRAINT fk_artista_solista
        FOREIGN KEY (artista)
            REFERENCES artista (isni)
                ON DELETE CASCADE
);
--
-- ----------------------------------------------------------------------------
--
CREATE TABLE artista_grupo ( -- RIA-10: Solista AND Grupo COVER Artista.
    artista,
--
    CONSTRAINT pk_artista_grupo
        PRIMARY KEY (artista),
--
    CONSTRAINT fk_artista_grupo
        FOREIGN KEY (artista)
            REFERENCES artista (isni)
                ON DELETE CASCADE
);
--
-- ----------------------------------------------------------------------------
--
CREATE TABLE album (
    mbid           VARCHAR (36), 
    titulo         VARCHAR (40)  CONSTRAINT nn_album_titulo         NOT NULL,
    tipo           VARCHAR (6)   CONSTRAINT nn_album_tipo           NOT NULL, -- RIA-02: O tipo de um álbum 
                                                                              -- tem de ser single, EP, ou LP.
    ano_lancamento NUMBER  (4)   CONSTRAINT nn_album_ano_lancamento NOT NULL,
    artista,                     -- ARTISTA QUE INTERPRETA ALBUM
--
    CONSTRAINT pk_album
        PRIMARY KEY (mbid),
--
    CONSTRAINT fk_album_artista
        FOREIGN KEY (artista)
            REFERENCES artista (isni),
--
    CONSTRAINT ck_album_tipo
        CHECK (tipo = 'SINGLE' OR tipo = 'EP' OR tipo = 'LP'),
--
    CONSTRAINT ck_album_ano_lancamento
        CHECK (ano_lancamento > 0),
--
    CONSTRAINT ck_album_mbid  -- RIA-01: O código MBID de um álbum 
                              -- tem de ter 36 carateres.
        CHECK (LENGTH (mbid) = 36)
);
--
-- ----------------------------------------------------------------------------
--
CREATE TABLE suporte_fisico (
    tipo VARCHAR (7),
--
    CONSTRAINT pk_suporte_fisico
        PRIMARY KEY (tipo),
--
    CONSTRAINT ck_suporte_fisico_tipo -- RIA-11: O tipo de um suporte físico 
                                      -- tem de ser CD, vinil, ou cassete.
        CHECK (tipo = 'CD' OR tipo = 'VINIL' OR tipo = 'CASSETE')
);
--
-- ----------------------------------------------------------------------------
--
CREATE TABLE album_suporte_fisico ( -- PARTICIPA NA AGREGAÇÃO
    album,
    suporte_fisico,
--
    CONSTRAINT pk_album_suporte_fisico
        PRIMARY KEY (album, suporte_fisico),
--
    CONSTRAINT fk_album
        FOREIGN KEY (album)
            REFERENCES album (mbid),
--
    CONSTRAINT fk_suporte_fisico
        FOREIGN KEY (suporte_fisico)
            REFERENCES suporte_fisico (tipo)
);
--
-- ----------------------------------------------------------------------------
--
CREATE TABLE versao (
    ean        VARCHAR  (13),
    designacao VARCHAR  (40) CONSTRAINT nn_versao_designacao NOT NULL,
--
    CONSTRAINT pk_versao
        PRIMARY KEY (ean),
--
    CONSTRAINT ck_versao_ean -- RIA-12: O código EAN-13 da versão de um álbum 
                             -- tem de ter 13 dígitos e ser positivo.
        CHECK (ean > 0 AND LENGTH (ean) = 13)
);
--
-- ----------------------------------------------------------------------------
--
CREATE TABLE album_suporte_fisico_versao ( -- CORRESPONDE À AGREGAÇÃO
    album,
    suporte_fisico,
    versao,
--
    CONSTRAINT pk_album_suporte_fisico_versao
        PRIMARY KEY (album, suporte_fisico, versao),
--
    CONSTRAINT fk_asfv_album
        FOREIGN KEY (album)
            REFERENCES album (mbid),
--
    CONSTRAINT fk_asfv_suporte_fisico
        FOREIGN KEY (suporte_fisico)
            REFERENCES suporte_fisico (tipo),
--
    CONSTRAINT fk_asfv_versao
        FOREIGN KEY (versao)
            REFERENCES versao (ean)
);
--
-- ----------------------------------------------------------------------------
--
CREATE TABLE utilizador (
    username        VARCHAR (40),
    email           VARCHAR (40) CONSTRAINT nn_utilizador_email           NOT NULL,
    palavra_passe   VARCHAR (40) CONSTRAINT nn_utilizador_palavra_passe   NOT NULL,
    data_nascimento DATE         CONSTRAINT nn_utilizador_data_nascimento NOT NULL,
    artista,                     -- ARTISTA FAVORITO
--
    CONSTRAINT pk_utilizador
        PRIMARY KEY (username), 
--
    CONSTRAINT un_utilizador_email -- RIA-13: O endereço de e-mail identifica 
                                   -- univocamente um utilizador.
        UNIQUE (email),
--
    CONSTRAINT fk_utilizador_artista
        FOREIGN KEY (artista)
            REFERENCES artista (isni)
);
--
-- ----------------------------------------------------------------------------
--
CREATE TABLE lista_personalizada ( -- ENTIDADE FRACA
    nome VARCHAR (40) CONSTRAINT nn_lista_personalizada_nome NOT NULL,
    utilizador,
--
    CONSTRAINT pk_lista_personalizada
        PRIMARY KEY (nome, utilizador),
--
    CONSTRAINT fk_lista_utilizador
        FOREIGN KEY (utilizador)
            REFERENCES utilizador (username)
                ON DELETE CASCADE
);
--
-- ----------------------------------------------------------------------------
--
CREATE TABLE possui (
    utilizador,
    versao,
    data_adicao DATE CONSTRAINT nn_utilizador_data_adicao NOT NULL,
--
    CONSTRAINT pk_possui
        PRIMARY KEY (utilizador, versao),
--
    CONSTRAINT fk_possui_versao
        FOREIGN KEY (versao)
            REFERENCES versao (ean),
--
    CONSTRAINT fk_possui_utilizador
        FOREIGN KEY (utilizador)
            REFERENCES utilizador (username)
);
--
-- ----------------------------------------------------------------------------
--
CREATE TABLE refere (
    lista_personalizada,
    utilizador,
    album,
--
    CONSTRAINT pk_refere
        PRIMARY KEY (lista_personalizada, album),
--
    CONSTRAINT fk_refere_album
        FOREIGN KEY (album)
            REFERENCES album (mbid),
--
    CONSTRAINT fk_refere_lista_personalizada
        FOREIGN KEY (lista_personalizada, utilizador)
            REFERENCES lista_personalizada (nome, utilizador)
);
--
-- ----------------------------------------------------------------------------
--
CREATE TABLE membro (
    artista_solista,
    artista_grupo,
--
    CONSTRAINT pk_membro
        PRIMARY KEY (artista_solista,artista_grupo),
--
    CONSTRAINT fk_membro_solista
        FOREIGN KEY (artista_solista)
            REFERENCES artista_solista (artista) 
		ON DELETE CASCADE,
--
    CONSTRAINT fk_membro_grupo
        FOREIGN KEY (artista_grupo)
            REFERENCES artista_grupo (artista) 
		ON DELETE CASCADE
);
--

-- ----------------------------------------------------------------------------
-- COMANDOS SQL-DML
-- ----------------------------------------------------------------------------
-- Criação de artistas
INSERT INTO artista (isni, nome, ano_atividade) 
    VALUES ('0000000000000001', 'Adele', 2006);
--
INSERT INTO artista (isni, nome, ano_atividade) 
    VALUES ('0000000000000002', 'The Beatles', 1960);
--
-- ----------------------------------------------------------------------------
-- Hierarquias de artista
INSERT INTO artista (isni, nome, ano_atividade) 
    VALUES ('0000000000000003', 'Josué Dias', 2015);
--
INSERT INTO artista_solista (artista) 
    VALUES ('0000000000000003');
--
INSERT INTO artista (isni, nome, ano_atividade) 
    VALUES ('0000000000000004', 'Coldplay', 2010);
--
INSERT INTO artista_grupo (artista) 
    VALUES ('0000000000000004');
--
INSERT INTO artista_solista (artista) 
    VALUES ('0000000000000001');
--
INSERT INTO artista_grupo (artista) 
    VALUES ('0000000000000002');
--
-- ----------------------------------------------------------------------------
-- Testes da tabela album
INSERT INTO album (mbid, titulo, tipo, ano_lancamento, artista)
    VALUES ('a0000000-0000-0000-0000-000000000001', '21', 'LP', 2011, '0000000000000001');
--
INSERT INTO album (mbid, titulo, tipo, ano_lancamento, artista)
    VALUES ('a0000000-0000-0000-0000-000000000002', 'Love Me Do', 'SINGLE', 1962, '0000000000000002');
--
INSERT INTO album (mbid, titulo, tipo, ano_lancamento, artista)
    VALUES ('a0000000-0000-0000-0000-000000000003', 'Sabes como é que é', 'EP', 2020, '0000000000000003');
--
-- ----------------------------------------------------------------------------
-- Criação dos suportes fisicos
INSERT INTO suporte_fisico (tipo) 
    VALUES ('CD');
--
INSERT INTO suporte_fisico (tipo) 
    VALUES ('VINIL');
--
INSERT INTO suporte_fisico (tipo) 
    VALUES ('CASSETE');
--
-- ----------------------------------------------------------------------------
-- Adicionar Album a um Suporte fisico
INSERT INTO album_suporte_fisico (album, suporte_fisico)
    VALUES ('a0000000-0000-0000-0000-000000000001', 'CD');
--
INSERT INTO album_suporte_fisico (album, suporte_fisico)
    VALUES ('a0000000-0000-0000-0000-000000000001', 'VINIL');
--
INSERT INTO album_suporte_fisico (album, suporte_fisico)
    VALUES ('a0000000-0000-0000-0000-000000000003', 'CASSETE');
--
-- ----------------------------------------------------------------------------
-- Testes de Versao
INSERT INTO versao (ean, designacao) 
    VALUES ('1234567890123', 'Versao Limitada');
--
INSERT INTO versao (ean, designacao) 
    VALUES ('9876543210987', 'Versao Standard');
--
-- ----------------------------------------------------------------------------
-- Versão de um album num determinado suporte fisico
INSERT INTO album_suporte_fisico_versao (album, suporte_fisico, versao)
    VALUES ('a0000000-0000-0000-0000-000000000001', 'CD', '1234567890123');
--
INSERT INTO album_suporte_fisico_versao (album, suporte_fisico, versao)
    VALUES ('a0000000-0000-0000-0000-000000000001', 'VINIL', '9876543210987');
--
-- ----------------------------------------------------------------------------
-- Testes da criação de utilizadores
INSERT INTO utilizador (username, email, palavra_passe, data_nascimento, artista)
    VALUES ('nelson', 'nelson@mail.com', 'multimetro', DATE '1990-05-15', '0000000000000001');
--
INSERT INTO utilizador (username, email, palavra_passe, data_nascimento, artista)
    VALUES ('mario', 'mario@mail.com', 'bit', DATE '2000-11-20', '0000000000000003');
--
-- ----------------------------------------------------------------------------
-- Lista Personalizada do utilizador
INSERT INTO lista_personalizada (nome, utilizador) 
    VALUES ('Musicas para karate', 'nelson');
--
INSERT INTO lista_personalizada (nome, utilizador) 
    VALUES ('Bangers de meditação', 'mario');
--
-- ----------------------------------------------------------------------------
-- Utilizador possui versao do album
INSERT INTO possui (utilizador, versao, data_adicao) 
    VALUES ('nelson', '1234567890123', DATE '2024-01-10');
--
INSERT INTO possui (utilizador, versao, data_adicao) 
    VALUES ('mario', '9876543210987', DATE '2024-02-25');
--
-- ----------------------------------------------------------------------------
-- Lista Personalizada refere determinado album
INSERT INTO refere (lista_personalizada, utilizador, album)
    VALUES ('Musicas para karate', 'nelson', 'a0000000-0000-0000-0000-000000000003');
--
INSERT INTO refere (lista_personalizada, utilizador, album)
    VALUES ('Bangers de meditação', 'mario', 'a0000000-0000-0000-0000-000000000002');
--
-- ----------------------------------------------------------------------------
-- Solista membro de Grupo
INSERT INTO membro (artista_solista, artista_grupo)
    VALUES ('0000000000000001', '0000000000000002');
--
INSERT INTO membro (artista_solista, artista_grupo)
    VALUES ('0000000000000003', '0000000000000004');
--
-- ----------------------------------------------------------------------------
COMMIT;
-- ----------------------------------------------------------------------------
