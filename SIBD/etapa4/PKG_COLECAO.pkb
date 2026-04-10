-- ----------------------------------------------------------------------------
-- SIBD - SISTEMAS DE INFORMAÇÃO E BASES DE DADOS
-- Etapa 4 do Projeto de 2025/2026
-- Grupo 25: Rafael Pedro fc63697, Josué Dias fc63699, Gabriel Brilha fc63792
-- --------------------------------------------------------------------------
CREATE OR REPLACE PACKAGE BODY PKG_COLECAO IS
    --
    -- --------------------------------------------------------------------------
    -- Funções auxiliares
    -- --------------------------------------------------------------------------

    -- --------------------------------------------------------------------------
    -- Evitar repetição de código. Conta quantos álbuns um utilizador
    -- tem na tabela 'possui'. Usada no retorno de regista/remove_posse.
    FUNCTION conta_total_albuns (
	utilizador_in IN possui.utilizador%TYPE)
    RETURN NUMBER
    IS
	total_albuns NUMBER;
    BEGIN
	SELECT COUNT(*) INTO total_albuns
	  FROM possui
	 WHERE utilizador = utilizador_in;
	
    RETURN total_albuns;
    END conta_total_albuns;

    -- --------------------------------------------------------------------------
    -- Função auxiliar para tratar de exceções, tornando estas mais intelígiveis
    PROCEDURE trata_excecao
    IS
    BEGIN

	IF SQLCODE = -1 THEN 
	    RAISE_APPLICATION_ERROR(-20002, 'Chave primária violada. Este registo já existe no sistema.'); -- PRIMARY KEY
	ELSIF SQLCODE = -2291 THEN 
	    RAISE_APPLICATION_ERROR(-20001, 'Chave estrangeira violada. Esta referência não existe no sistema.'); -- FOREIGN KEY
	ELSIF SQLCODE = -1400 THEN 
	    RAISE_APPLICATION_ERROR(-20007, 'Campo obrigatório. Este valor não pode ser nulo.'); -- VALORES NOT NULL
	ELSIF SQLCODE = -2290 THEN 
	    RAISE_APPLICATION_ERROR(-20008, 'Regras da tabela violadas. Verifique as restrições.'); -- CHECK DAS TABELAS
	ELSE 
	    RAISE_APPLICATION_ERROR(-20009, 'Erro obtido, tenta novamente: ' || SQLERRM); -- OUTROS ERROS, LANÇA MSG DE ERRO
	END IF; 
    END trata_excecao;
    -- --------------------------------------------------------------------------

    -- --------------------------------------------------------------------------
    -- Implementação
    -- --------------------------------------------------------------------------

    -- --------------------------------------------------------------------------
    -- Inserir um novo artista na base de dados
    PROCEDURE regista_artista(
	isni_in IN artista.isni%TYPE,
	nome_in IN artista.nome%TYPE,
	inicio_in IN artista.inicio%TYPE) 
    IS
    BEGIN
	INSERT INTO artista (isni, nome, inicio)
	    VALUES (isni_in, nome_in, inicio_in);
    EXCEPTION
	WHEN OTHERS THEN
	    trata_excecao;
    END regista_artista;

    -- --------------------------------------------------------------------------
    -- Inserir um álbum, de forma a validar a coerência temporal com o artista.
    PROCEDURE regista_album(
	ean_in IN album.ean%TYPE,
	titulo_in IN album.titulo%TYPE,
	tipo_in IN album.tipo%TYPE,
	ano_in IN album.ano%TYPE,
	artista_in IN album.artista%TYPE,
	suporte_in IN album.suporte%TYPE,
	versao_in IN album.versao%TYPE := NULL)
    IS
	-- Variável local que guarda o inicio do artista para comparação (RIA 08)
	ano_inicio_artista artista.inicio%TYPE;  
    BEGIN
	-- Consulta para variável local
	SELECT inicio INTO ano_inicio_artista 	  FROM artista
	 WHERE (isni = artista_in);

	IF (ano_in < ano_inicio_artista) THEN -- RIA 08
	    RAISE_APPLICATION_ERROR(-20003, 'Ano de lançamento do álbum deve ser maior ou igual ao ano de início do artista.');
	ELSE
	    -- Se válido, insere o registo
	    INSERT INTO album (ean, titulo, tipo, ano, artista, suporte, versao)
		VALUES (ean_in, titulo_in, tipo_in, ano_in, artista_in, suporte_in, versao_in);
	END IF;

    EXCEPTION
	WHEN NO_DATA_FOUND THEN 
	    RAISE_APPLICATION_ERROR(-20001, 'O artista que interpreta o álbum não tem registo no sistema'); -- FOREIGN KEY
	WHEN DUP_VAL_ON_INDEX THEN 
	    RAISE_APPLICATION_ERROR(-20002, 'Este álbum já tem registo no sistema'); -- PRIMARY KEY
	WHEN OTHERS THEN
	    trata_excecao;
    END regista_album;


    -- --------------------------------------------------------------------------
    -- Remover um artista e, em cascata, os seus álbuns.
    PROCEDURE remove_artista(isni_in IN artista.isni%TYPE)
    IS
    BEGIN  
   -- Se o artista a remover é o favorito de alguem, tornar esse valor NULL
	UPDATE utilizador
	SET artista = NULL
	WHERE artista = isni_in;

	-- Procurar e apagar álbuns realizados por este artista
	FOR arti IN (SELECT ean FROM album WHERE artista = isni_in) LOOP
	    remove_album(arti.ean);
	END LOOP;

        -- Remover artista do sistema
	DELETE 
	  FROM artista 
	 WHERE (isni = isni_in); 

	-- Validar se o artista existe
	IF SQL%ROWCOUNT = 0 THEN
	    RAISE_APPLICATION_ERROR(-20005, 'O artista que pretende remover não existe no sistema.');
	END IF;
    END remove_artista;

    -- --------------------------------------------------------------------------
    -- Remover um álbum e, em cascata, as suas posses.
    PROCEDURE remove_album(ean_in IN album.ean%TYPE)
    IS
	-- Variável dummy para receber o retorno da função
	n NUMBER;
    BEGIN
        -- obter os utilizadores que tem este album. Apagar da coleção
	FOR uti IN (SELECT utilizador FROM possui WHERE album = ean_in) LOOP 
	    n := remove_posse(uti.utilizador, ean_in);
	END LOOP;

	-- Apagar album do sistema
	DELETE 
	  FROM album 
	 WHERE (ean = ean_in);

	-- Validar existência
	IF SQL%ROWCOUNT = 0 THEN
	    RAISE_APPLICATION_ERROR(-20005, 'O álbum que pretende remover não existe no sistema.');
	END IF;
    END remove_album;

    -- --------------------------------------------------------------------------
    -- Registar utilizador com validação de idade e distinção de erros únicos.
    PROCEDURE regista_utilizador ( 
	username_in   IN utilizador.username%TYPE,
	email_in      IN utilizador.email%TYPE,
	senha_in      IN utilizador.senha%TYPE,
	nascimento_in IN utilizador.nascimento%TYPE,
	artista_in    IN utilizador.artista%TYPE := NULL)
    IS
	ano_atual NUMBER(4);
    BEGIN
	ano_atual := TO_NUMBER(TO_CHAR(SYSDATE, 'YYYY'));

	-- Validação RIA 15: Utilizador tem de ter 13 anos ou mais
	IF (ano_atual - nascimento_in < 13) THEN
	    RAISE_APPLICATION_ERROR(-20010, 'O utilizador tem de ter 13 ou mais anos.'); -- RIA-15
	END IF;

	INSERT INTO utilizador (username, email, senha, nascimento, artista)
	    VALUES (username_in, email_in, senha_in, nascimento_in, artista_in);

    EXCEPTION
	WHEN DUP_VAL_ON_INDEX THEN
	    -- Tratamento específico para distinguir erro de Email vs Username duplicado
	    IF (REGEXP_SUBSTR(SQLERRM, 'un_utilizador_email') IS NOT NULL) THEN
		RAISE_APPLICATION_ERROR(-20002, 'Já existe um utilizador com esse email');
	    ELSE
		RAISE_APPLICATION_ERROR(-20002, 'Username já existe');
	    END IF;
	WHEN OTHERS THEN
	    -- Tratamento específico para Artista Favorito inexistente
	    IF (REGEXP_SUBSTR(SQLERRM, 'fk_utilizador_artista') IS NOT NULL) THEN
		RAISE_APPLICATION_ERROR(-20001, 'O artista favorito do utilizador não existe no sistema');
	    ELSE
		trata_excecao;
	    END IF;
    END regista_utilizador;

    -- --------------------------------------------------------------------------
    -- Remover utilizador e as suas posses primeiro.
    PROCEDURE remove_utilizador(username_in IN utilizador.username%TYPE)
    IS
	-- Percorrer a coleção do utilizador e remover cada posse
	CURSOR c_albuns_utilizador IS
	    SELECT album
	      FROM possui
	     WHERE utilizador = username_in;

	quantidade_albuns_restantes NUMBER;
    BEGIN
	FOR registo IN c_albuns_utilizador LOOP
	    quantidade_albuns_restantes := remove_posse(username_in, registo.album);
	END LOOP;

	-- Remover o utilizador
	DELETE 
	  FROM utilizador
	 WHERE username = username_in;

	-- Validar se existia
	IF SQL%ROWCOUNT = 0 THEN
	    RAISE_APPLICATION_ERROR(-20005, 'O utilizador que pretende remover não existe no sistema.');
	END IF;
    END remove_utilizador;

    -- --------------------------------------------------------------------------
    -- Adicionar um álbum à coleção, valida datas (RIA 16 e 17).
    FUNCTION regista_posse(
	utilizador_in IN possui.utilizador%TYPE,
	album_in      IN possui.album%TYPE,
	desde_in      IN possui.desde%TYPE DEFAULT SYSDATE) 
    RETURN NUMBER
    IS
       ano_lancamento album.ano%TYPE;
       ano_nascimento utilizador.nascimento%TYPE;
       total_albuns   NUMBER;
    BEGIN
	-- Obter dados necessários para validação
	SELECT ano, nascimento    
          INTO ano_lancamento, ano_nascimento
	  FROM album , utilizador 
         WHERE (ean = album_in)
	   AND (username = utilizador_in);

        -- RIA 16: Data de posse não pode ser anterior ao lançamento do álbum
	IF( EXTRACT(YEAR FROM desde_in) < ano_lancamento ) THEN    -- RIA 16
	    RAISE_APPLICATION_ERROR(-20004, 'O ano da data de posse tem de ser posterior ou igual ao ano de lançamento do álbum');
	END IF;
    
        -- RIA 17: Utilizador tinha de ter pelo menos 13 anos na data da posse
	IF (EXTRACT(YEAR FROM desde_in) < (ano_nascimento + 13 )) THEN   -- RIA 17
	    RAISE_APPLICATION_ERROR (-20005, 'O ano da data de posse tem que ser posterior ou igual ao ano que o utilizador perfaz 13');
	END IF;

	-- inserir o registo de posse (*)
	INSERT INTO possui (utilizador, album, desde)      
	    VALUES (utilizador_in, album_in, desde_in);

    RETURN conta_total_albuns(utilizador_in);
    EXCEPTION 
	WHEN NO_DATA_FOUND THEN 
	    RAISE_APPLICATION_ERROR(-20001, 'O utilizador ou o álbum não têm registo no sistema.');
	WHEN DUP_VAL_ON_INDEX 
	    THEN RAISE_APPLICATION_ERROR(-20002, 'O utilizador já possui o álbum na sua coleção.');
	WHEN OTHERS THEN 
	    trata_excecao;
    END regista_posse;

    -- --------------------------------------------------------------------------
    -- Remover um álbum da coleção de um utilizador.
    FUNCTION remove_posse(
	utilizador_in IN possui.utilizador%TYPE,
	album_in      IN possui.album%TYPE) 
    RETURN NUMBER
    IS 
        -- total de alguns que fica , valor a de retorno
	total_albuns NUMBER;   
    BEGIN 
        -- remover o registo da posse
	DELETE 
	  FROM possui  
	 WHERE utilizador = utilizador_in
	   AND album = album_in;
    
	IF (SQL%ROWCOUNT = 0) THEN
	    RAISE_APPLICATION_ERROR(-20005, 'Dados inválidos para remoção de posse do utilizador.');
	END IF;
    
	RETURN conta_total_albuns(utilizador_in);

    EXCEPTION 
	WHEN OTHERS THEN
	    trata_excecao; 
    END remove_posse;

    -- --------------------------------------------------------------------------
    -- Devolver um cursor com a coleção do utilizador.
    -- Inclui lógica para marcar o artista favorito com '*'.
    FUNCTION lista_albuns(utilizador_in IN utilizador.username%TYPE)
	RETURN SYS_REFCURSOR
    IS
	-- Declarar cursor
	c_albuns SYS_REFCURSOR;
    BEGIN
	-- Abertura do cursor
	-- Query definida no interior da abertura do cursor
	OPEN c_albuns FOR
	    SELECT AL.ean, 
		   AL.titulo, 
		   AL.tipo, 
		   AL.ano, 
		   AL.suporte, 
		   AL.versao,
		   -- Se o artista for o favorito do utilizador aparece com asterisco
		   CASE
		       WHEN AL.artista = UT.artista THEN AR.nome || '*'
		       ELSE AR.nome
		   END AS nome_artista
	      FROM album AL, possui PO, utilizador UT, artista AR
	     WHERE (UT.username = utilizador_in)
	       AND (PO.utilizador = utilizador_in)
	       AND (UT.username = PO.utilizador)
	       AND (PO.album = AL.ean)
	       AND (AL.artista = AR.isni)
	    ORDER BY
		AL.ano DESC,
		AR.nome ASC,
		AL.titulo ASC;

	-- Cursor devolvido (aberto)
    RETURN c_albuns;
    END lista_albuns;
    -- --------------------------------------------------------------------------
END PKG_COLECAO;
/












