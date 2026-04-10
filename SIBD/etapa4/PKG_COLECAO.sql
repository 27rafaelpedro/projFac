-- ----------------------------------------------------------------------------
-- SIBD - SISTEMAS DE INFORMAÇÃO E BASES DE DADOS
-- Etapa 4 do Projeto de 2025/2026
-- Grupo 25: Rafael Pedro fc63697, Josué Dias fc63699, Gabriel Brilha fc63792
-- 
-- Contribuições: 
-- Rafael Pedro: 33.33% - regista_artista, remove_artista, regista_album, remove_album
-- Josué Dias: 33.33% -  ficheiro sql, regista_posse, remove_posse
-- Gabriel Brilha: 33.33% - regista_utilizador, remove_utilizador e Cursor e PKS
-- ----------------------------------------------------------------------------


-- INSERÇÕES --

-- REGISTO DE ARTISTAS --
EXEC PKG_COLECAO.regista_artista('0000000101701234', 'The Millennium', 2000); 
EXEC PKG_COLECAO.regista_artista('0000000101705678', 'The Classics', 1990);

-- REGISTO DE ÁLBUNS --
EXEC PKG_COLECAO.regista_album('1234567890123', 'Future Sounds', 'LP', 2005, '0000000101701234', 'CD'); 
EXEC PKG_COLECAO.regista_album('9876543210987', 'Old School Hits', 'EP', 1995, '0000000101705678', 'vinil');

-- REGISTO DE UTILIZADORES --
EXEC PKG_COLECAO.regista_utilizador('mr_calha', 'calhabit@mail.com', 'senha123', 1990, NULL);
EXEC PKG_COLECAO.regista_utilizador('euro_digital', 'eur@mail.com', 'pass123', 2005, '0000000101701234');

-- REGISTO DE POSSE --
DECLARE
    v_total NUMBER;
BEGIN
   
    v_total := PKG_COLECAO.regista_posse('mr_calha', '1234567890123', DATE '2010-01-01');
    v_total := PKG_COLECAO.regista_posse('euro_digital', '9876543210987', DATE '2024-01-01');
END;
/

-- TESTAR EXCEÇÕES --

-- ADICIONAR UM ARTISTA JÁ EXISTENTE (ESPERA OBTER-SE 20002 (VIOLAÇÃO DA CHAVE PRIMÁRIA)) --
EXEC PKG_COLECAO.regista_artista('0000000101701234', 'The Millennium', 2000); 

-- INSERIR UM ÁLBUM DE UM ARTISTA QUE NÃO ESTÁ REGISTADO (ESPERA OBTER-SE 20001 (VIOLAÇÃO DA CHAVE ESTRANGEIRA)) --
EXEC PKG_COLECAO.regista_album('1111111111111', 'Erro FK Artista', 'LP', 2025, '9999999999999', 'CD');

-- INSERIR UM ÁLBUM COM UM TIPO NÃO ACEITADO NA TABELA (ESPERA-SE OBTER 20008 (VIOLAÇÃO DE CHECK)) --
EXEC PKG_COLECAO.regista_album('4444444444444', 'Bad Format', 'tipo12', 2010, '0000000101701234', '8-track');

-- INSERIR UM ÁLBUM COM UM ISNI QUE NÃO RESPEITA AS RESTRIÇÕES DE TAMANHO (VIOLAÇÃO DE TAMANHO DE ATRIBUTO) --
EXEC PKG_COLECAO.regista_album('4444444444444123456', 'Bad Format', 'tipo123', 2010, '0000000101701234', '8-track');

-- UTILIZADOR REGISTADO SEM E-MAIL (ESPERA OBTER-SE 20007 (VIOLAÇÃO NOT NULL)) --
EXEC PKG_COLECAO.regista_utilizador('util_sem_mail', NULL, 'pass123', 1995, NULL);

-- REGISTAR ARTISTA SEM NOME (VIOLAÇÃO NOT NULL) --
EXEC PKG_COLECAO.regista_artista('0000000101708888', NULL, 2020);

-- REGISTAR UM ARTISTA COM UM NOME QUE NÃO RESPEITA AS RESTRIÇÕES (ESPERA OBTER-SE 20009 (VIOLAÇÃO DE EXCEÇÕES GERAIS)) --
EXEC PKG_COLECAO.regista_artista('0000000101700000', 'AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA', 2025);


-- REMOÇÕES --

-- REMOVER UM UTILIZADOR --
EXEC PKG_COLECAO.remove_utilizador('mr_calha');

-- REMOVER UMA POSSE --
DECLARE
    v_total NUMBER;
BEGIN
   
    v_total := PKG_COLECAO.remove_posse ('euro_digital', '9876543210987');
END;
/

-- REMOVER UM ALBUM --
EXEC PKG_COLECAO.remove_album ('1234567890123');

-- REMOVER UM ARTISTA -- 
EXEC PKG_COLECAO.remove_artista ('0000000101701234');

-- REMOVER UM ARTISTA QUE NÃO EXISTE
EXEC PKG_COLECAO.remove_artista('0000000101708888');
