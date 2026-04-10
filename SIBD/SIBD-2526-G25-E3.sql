-- ----------------------------------------------------------------------------
-- SIBD - SISTEMAS DE INFORMAÇÃO E BASES DE DADOS
-- Etapa 3 do Projeto de 2025/2026
-- Grupo 25: Rafael Pedro fc63697, Josué Dias fc63699, Gabriel Brilha fc63792
-- 
-- Contribuições: 
-- Rafael Pedro:   33.33% - Desenvolvimento da Query 3
-- Josué Dias:     33.33% - Desenvolvimento da Query 1
-- Gabriel Brilha: 33.33% - Desenvolvimento da Query 2
-- Todos:                   Colaboração na Query 4
--
-- ----------------------------------------------------------------------------
--
-- Query 1 - Username e e-mail dos utilizadores nascidos em 1990, que têm como artista
--           favorito Day6, e que, durante o ano de 2025, registaram a posse de um ou mais
--           álbuns interpretados por esse artista. O EAN-13, título, e ano de lançamento
--           dos álbuns também devem ser mostrados, bem como o número de dias que
--           passaram desde a data dos registos de posse. A ordenação do resultado deve
--           ser ascendente pelo username dos utilizadores e descendente pelo número de
--           dias que passaram desde os registos de posse e pelo EAN-13 dos álbuns.
--
SELECT UT.username, 
       UT.email, 
       AL.ean, 
       AL.ano, 
       AL.titulo, 
       ROUND( SYSDATE - PO.desde) AS dias_desde_posse
--
  FROM utilizador UT, possui PO, album AL, artista AR
 WHERE (UT.nascimento = 1990)              -- só utilizadores nacsidos em 1990
   AND (AR.nome = 'Day6')                  -- o nome do artista ser 'Day6'
   AND (UT.username = PO.utilizador)       -- junção entre o username do Utilizador e o utilizador do Possui
   AND (AL.artista = AR.isni)              -- junção entre o artista do Album com o isni do Artista 
   AND (UT.artista = AR.isni)              -- junção entre o artista do Utilizador e o isni do Artista
   AND (PO.album = AL.ean)                 -- junção entre possui e album
   AND TO_CHAR(PO.desde, 'YYYY') = '2025'
--
ORDER BY UT.username ASC,                  -- Ordenação ascendente por username
         ROUND(SYSDATE - PO.desde) DESC,   -- Ordenação descendente pelo número de dias
         AL.ean DESC;                      -- Ordenação descendente pelo EAN-13 do álbum
--
-- ----------------------------------------------------------------------------
--
-- Query 2 - Username dos utilizadores com endereço de e-mail no gmail.com que, consi-
--           derando apenas os registos de posse efetuados entre 2000 e 2020, ou não pos-
--           suem álbuns interpretados pelo artista Dire Straits, ou possuem no máximo
--           três álbuns desse artista. Adicionalmente, os utilizadores não podem possuir
--           álbuns do tipo single, seja qual for o artista e o ano do registo. O resultado deve
--           vir ordenado pelo username de forma ascendente.
--
SELECT DISTINCT UT.username
  FROM utilizador UT
 WHERE (UT.email LIKE '_%@gmail.com') -- apenas utilizadores de dominio mail gmail.com
   AND (SELECT COUNT(*)               -- selecionar utilizadores com 0 ou no maximo 3 albuns dos Dire Straits
          FROM possui PO2, album AL2, artista AT
         WHERE (PO2.utilizador = UT.username) -- considera apenas as posses do utilizador atual
	   AND (PO2.album = AL2.ean)
	   AND (AL2.artista = AT.isni)
	   AND (AT.nome = 'Dire Straits')   -- Limita a contagem ao artista 'Dire Straits'
	   AND TO_CHAR(PO2.desde, 'YYYY') BETWEEN '2000' AND '2020') <= 3 -- Valida se a contagem é 0 ou no máximo 3 nesse período
--
   AND NOT EXISTS (SELECT *           -- ignorar utilizadores com singles, independentemente do ano
                     FROM possui PO1, album AL1
		    WHERE (PO1.utilizador = UT.username)
	              AND (PO1.album = AL1.ean)
		      AND (AL1.tipo = 'single')) 
--
ORDER BY UT.username ASC; -- Ordenação ascendente pelo username
--
-- ----------------------------------------------------------------------------
--
-- Query 3 - Nome e ano de início de atividade de artistas tais que todos os utilizadores nas-
--           cidos de 2000 em diante tenham registado a posse de pelo menos um álbum
--           interpretado por esses artistas, com as seguintes restrições adicionais: só ar-
--           tistas que tenham lançado um ou mais álbuns nos dois últimos anos, e os re-
--           gistos de posse dos utilizadores têm de ter sido feitos entre as 12h e as 19h59.
--           O resultado deve vir ordenado por nome de artista de forma ascendente e pelo
--           seu ano de início de atividade de forma descendente. Nota: a data de um registo
--           de posse de álbum também guarda as horas e minutos.
--
SELECT A.nome as Artista, A.inicio as ano_inicio_atividade
  FROM artista A
 WHERE (
    NOT EXISTS(SELECT U.username  -- utilizadores que nascem de 2000 para a frente. EXISTS não é suficiente para totalidade.
                 FROM utilizador U 
                WHERE (U.nascimento >= 2000)
                  AND NOT EXISTS(SELECT *   -- Utilizadores que possuem pelo menos um album no intervalo 
                                   FROM possui P, Album Al  
                                  WHERE (P.utilizador = U.username) 
                                    AND (Al.artista = A.isni) 
                                    AND (Al.ean = p.album)
                                    AND (TO_CHAR(P.desde, 'HH24') BETWEEN '12' AND '19')))) -- hora válida
                  AND EXISTS (SELECT *    -- Existe pelo menos um album lançado pelo artista nos utlitmos 2 anos 
                                FROM album Al2
                               WHERE (A.isni = Al2.artista)
                                 AND (Al2.ano >= (EXTRACT(YEAR FROM CURRENT_DATE) - 2))) -- Album lançado nos ultimos 2 anos
--
ORDER BY A.nome ASC, A.inicio DESC;
--
-- ----------------------------------------------------------------------------
--
-- Query 4 - Username dos utilizadores com mais registos de posse de álbuns em suporte
--           vinil em cada ano, separadamente para utilizadores nascidos nas décadas de
--           1980 e 1990, devendo a década de nascimento dos utilizadores e o número total
--           de registos de posse de álbuns em cada ano também aparecer no resultado. A
--           ordenação do resultado deve ser descendente pelo ano e ascendente pela dé-
--           cada de nascimento dos utilizadores. No caso de haver mais do que um utiliza-
--           dor nascido na mesma década e com o mesmo número máximo de registos de
--           posse de álbuns num ano, devem ser mostrados todos esses utilizadores. Nota:
--           por conveniência, está disponível a função decada_de_ano, que arredonda um
--           ano com quatro dígitos à década; por exemplo, decada_de_ano(2025) = 2020.
--
SELECT TO_CHAR(PO1.desde, 'YYYY') AS ano_registo, 
       decada_de_ano(UT1.nascimento) AS decada, 
       UT1.username, 
       COUNT(*) AS total_registos
--
  FROM utilizador UT1, possui PO1, album AL1
 WHERE (UT1.username = PO1.utilizador) -- Ligação entre utilizador e tabela de posse
   AND (AL1.ean = PO1.album)           -- Ligação entre álbum e tabela de posse
   AND (AL1.suporte = 'vinil')         -- Restringe a contagem apenas a álbuns de suporte vinil
   AND (decada_de_ano(UT1.nascimento) = 1980 OR decada_de_ano(UT1.nascimento) = 1990) -- Filtra utilizadores nascidos nas décadas pretendidas
--
 GROUP BY TO_CHAR(PO1.desde, 'YYYY'), decada_de_ano(UT1.nascimento), UT1.username
HAVING (COUNT(*) = (SELECT MAX(COUNT(*)) -- Conta apenas se o total for igual ao máximo 
	              FROM utilizador UT2, possui PO2, album AL2 
		     WHERE (UT2.username = PO2.utilizador) 
		       AND (PO2.album = AL2.ean) 
		       AND (AL2.suporte = 'vinil') -- O cálculo do máximo também deve ser só para vinis
		       AND (decada_de_ano(UT2.nascimento) = decada_de_ano(UT1.nascimento)) -- Correlação: compara com a mesma década
                       AND (TO_CHAR(PO2.desde, 'YYYY') = TO_CHAR(PO1.desde, 'YYYY'))       -- Correlação: compara com o mesmo ano
		     GROUP BY UT2.username))
--
ORDER BY ano_registo DESC, decada ASC;
--
-- ----------------------------------------------------------------------------

