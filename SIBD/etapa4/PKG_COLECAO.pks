-- ----------------------------------------------------------------------------
-- SIBD - SISTEMAS DE INFORMAÇÃO E BASES DE DADOS
-- Etapa 4 do Projeto de 2025/2026
-- Grupo 25: Rafael Pedro fc63697, Josué Dias fc63699, Gabriel Brilha fc63792
-- ----------------------------------------------------------------------------
CREATE OR REPLACE PACKAGE PKG_COLECAO IS
    --
    --  Todas as operações lançam exceções para sinalizar casos de erro.
    --
    --  Exceção Mensagem
    --  -20001  Não existe esta referência no sistema. 
    --  -20002  Já existe este registo no sistema.
    --  -20003  Ano de lançamento do álbum deve ser maior ou igual ao ano de início do artista.
    --  -20004  O ano da data de posse tem de ser posterior ou igual ao ano de lançamento do álbum
    --  -20005  O registo que se pretende remover não existe no sistema.
    --  -20007  Este valor não pode ser nulo.
    --  -20008  Regras da tabela violadas.
    --  -20009  Outros erros.
    --  -20010  O utilizador tem de ter 13 ou mais anos.
    --

    -- --------------------------------------------------------------------------
    --  Exceções lançadas pelo regista_artista 
    --
    --
    PROCEDURE regista_artista(
	isni_in   IN artista.isni%TYPE,
	nome_in   IN artista.nome%TYPE,
	inicio_in IN artista.inicio%TYPE);

    -- --------------------------------------------------------------------------
    --  Exceções lançadas pelo regista_album
    --       -20001  Não existe esta referência no sistema. 
    --       -20002  Já existe este registo no sistema.
    --       -20003  Ano de lançamento do álbum deve ser maior ou igual ao ano de início do artista.
    --
    PROCEDURE regista_album(
	ean_in     IN album.ean%TYPE,
	titulo_in  IN album.titulo%TYPE,
	tipo_in    IN album.tipo%TYPE,
	ano_in     IN album.ano%TYPE,
	artista_in IN album.artista%TYPE,
	suporte_in IN album.suporte%TYPE,
	versao_in  IN album.versao%TYPE := NULL);

    -- --------------------------------------------------------------------------
    --  Exceções lançadas por remove_artista
    --        -20005  O registo que se pretende remover não existe no sistema.   
    --

    PROCEDURE remove_artista(isni_in in artista.isni%TYPE);

    -- --------------------------------------------------------------------------
    --  Exceções lançadas por remove_album
    --       -20005  O registo que se pretende remover não existe no sistema.
    --

    PROCEDURE remove_album(ean_in in album.ean%TYPE);

    -- --------------------------------------------------------------------------
    -- Exceções lançadas pelo regista_utilizador 
    --      -20001  Não existe esta referência no sistema.
    --      -20002  Já existe este registo no sistema.
    --      -20010  O utilizador tem de ter 13 ou mais anos.
    --       

    PROCEDURE regista_utilizador (
	username_in        IN utilizador.username%TYPE, 
	email_in           IN utilizador.email%TYPE, 
	senha_in           IN utilizador.senha%TYPE, 
	nascimento_in      IN utilizador.nascimento%TYPE, 
	artista_in         IN utilizador.artista%TYPE := NULL);

    -- --------------------------------------------------------------------------
    --  Exceções lançadas pelo remove_utilizador
    --      -20005  O registo que se pretende remover não existe no sistema
    --
    PROCEDURE remove_utilizador (
	username_in IN utilizador.username%TYPE);

    -- --------------------------------------------------------------------------
    --  Exceções lançadas pelo regista_posse
    --       -20001  Não existe esta referência no sistema. 
    --       -20002  Já existe este registo no sistema.
    --       -20004  O ano da data de posse tem de ser posterior ou igual ao ano de lançamento do álbum
    --       -20005  O registo que se pretende remover não existe no sistema.
    --    

    FUNCTION regista_posse(
	utilizador_in IN possui.utilizador%TYPE,
	album_in      IN possui.album%TYPE,
	desde_in      IN possui.desde%TYPE DEFAULT SYSDATE)
    RETURN NUMBER;

    -- --------------------------------------------------------------------------
    --  Exceções lançadas por remove_posse
    --       -20005  O registo que se pretende remover não existe no sistema.
    --

    FUNCTION remove_posse(
	utilizador_in IN possui.utilizador%TYPE,
	album_in      IN possui.album%TYPE)  
    RETURN NUMBER;

    -- --------------------------------------------------------------------------
    --  Exceções lançadas por lista_albuns
    --

    FUNCTION lista_albuns(utilizador_in IN utilizador.username%TYPE)
    RETURN SYS_REFCURSOR;

    -- --------------------------------------------------------------------------
END PKG_COLECAO;
/
