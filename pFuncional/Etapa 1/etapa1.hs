-- | Primeira etapa do projeto de Programação Funcional
-- | Trabalho realizado por:
-- | Rafael Pedro 63697
-- | Josué Dias 63699
-- | Grupo 4

-- | Divide uma lista em grupos de tamanho n
chunksOf :: Int -> [a] -> [[a]]
chunksOf n xs = [ take n (drop y xs) | y <- [0, n..(length xs - 1)] ] 

-- | Função de ativação de sigmoid
sigmoid :: Double -> Double
sigmoid x = 1/(1 + exp (-x))

-- | Derivada da função de ativação de sigmoid
-- | Onde x corresponde a sigmoid x
sigmoid' :: Double -> Double
sigmoid' x = x * (1 - x)

-- | Transpõe uma matriz
transpose :: [[a]] -> [[a]]
transpose xs = [ [n !! y | n <- xs] | y <- [0..(length (head xs) - 1)] ]

-- | Multiplica uma matriz por um vetor
multMatrix :: Num a => [[a]] -> [a] -> [a]
multMatrix m v = [ produtoEscalarAux v x | x <- m ]

-- | Função auxiliar a multMatrix
-- | Calcula o produto escalar entre uma linha da matriz e o vetor
produtoEscalarAux :: Num a => [a] -> [a] -> a
produtoEscalarAux x y = sum [ (x !! i) * (y !! i) | i <- [0 .. length x - 1] ]

-- | Soma ponto-a-ponto de dois vetores
somaVectorial :: [Double] -> [Double] -> [Double]
somaVectorial xs ys = [ head(drop a xs) + head(drop a ys) | a <- [0..(length xs - 1)] ]