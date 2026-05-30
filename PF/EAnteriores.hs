module EAnteriores (
  Layer(..), 
  Network(..), 
  buildNetwork, 
  forwardPass, 
  backPropagation, 
  msePredictions
  ) 
  where

-- | ETAPAS ANTERIORES

-- | Record Layer que respresenta uma camada com uma matriz de pesos
-- | e um vetor bias.
data Layer  = Layer{weight :: [[Double]], bias :: [Double]} deriving (Show, Read)

-- | Record Network que representa uma rede neuronal.
data Network = Network{layers :: [Layer]} deriving (Show, Read)

-- | Constrói uma rede neuronal.
-- | Exemplo : length (layers (buildNetwork 2 [4,1] (repeat 0.1))) == 2
buildNetwork :: Int -> [Int] -> [Double] -> Network
buildNetwork n nodes values = Network (buildLayers n nodes values)

-- | Constrói as camadas que constituem a rede neuronal.
buildLayers :: Int -> [Int] -> [Double] -> [Layer]     
buildLayers n [] values = []                     
buildLayers n (x:xs) values = camadaAtual : buildLayers n xs (drop (x * n + x) values)
     where
        matrizPesos = chunksOf n  (take (x * n ) values)
        bias = take x (drop (x * n) values )                  
        camadaAtual = Layer matrizPesos bias   

-- | Diferença entre previsão e alvo (por elemento).
-- | outputError [0.9] [1.0] == [-0.1]
outputError :: [Double] -> [Double] -> [Double]
outputError  = zipWith (-) 


-- | Erro quadrático médio entre previsão e alvo.
-- | Exemplo: mse [1.0] [0.0] == 1.0
mse :: [Double] -> [Double] -> Double
mse xs ys 
    | not (null xs) = mseRecursive xs ys * (1 / fromIntegral(length xs))
    | otherwise = 0.0


-- | Somatório auxiliar para mse.
mseRecursive :: [Double] -> [Double] -> Double
mseRecursive [] [] = 0.0
mseRecursive (x:xs) (y:ys) = (x - y)^2 + mseRecursive xs ys


-- | MSE médio sobre um conjunto de previsões.
-- | msePredictions [[1.0, 1.0]] [[1.0, 1.0]] == 0.0
msePredictions :: [[Double]] -> [[Double]] -> Double  
msePredictions a b = sum ( zipWith mse a b) /  fromIntegral (length a)


-- | Propaga a entrada pela rede e devolve todas as ativações.
-- | Exemplo : length (forwardPass [0.1] net) == length (layers net) + 1
forwardPass :: [Double] -> Network -> [[Double]]
forwardPass xs net = forwardPassAux xs (layers net)


-- | Função auxiliar para forwardPass
forwardPassAux :: [Double] -> [Layer] -> [[Double]]
forwardPassAux xs [] = [xs]
forwardPassAux xs (l:ls) = xs : forwardPassAux (applySigmoid xs (weight l) (bias l)) ls 


-- | Função auxiliar a forwardPassAux que aplica sigmoid(w*x + b) a cada linha da matriz de pesos
applySigmoid :: [Double] -> [[Double]] -> [Double] -> [Double]
applySigmoid _ [] _ = []
applySigmoid xs (w:ws) (b:bs) = sigmoid activate : applySigmoid xs ws bs
        where activate = sum(zipWith (*) w xs) + b


-- | Executa um passo de backpropagation dado uma taxa de aprendizagem, o
-- | input e o output esperado para um exemplo concreto.
backPropagation :: Double -> [Double] -> [Double] -> Network -> Network  
backPropagation  tax input output net = novaRede
        where 
            todasActs = forwardPass input net 
            previsao = last todasActs
            deltaSaida = outputError previsao output
            todosDeltas = allDeltas net todasActs deltaSaida
            novaRede = Network (zipWith3 (updateLayer tax) (layers net) (init todasActs) todosDeltas)

-- | Calcula todos os deltas de cada neurónio.
allDeltas :: Network -> [[Double]] -> [Double] -> [[Double]]
allDeltas net acts dSaida = 
    scanr calculaAnterior dSaida (zip (init (tail acts)) (tail (layers net)))
    where
      calculaAnterior (a, l) df = innerDelta a df (weight l)


-- | Para cada camada, autualizamos a lista de pesos e bias do seus neurónios.
updateLayer :: Double -> Layer -> [Double] -> [Double] -> Layer
updateLayer tax (Layer pesos bias) actsAnterior deltasAtual = Layer novosPesos novosBias
  where
    novosBias = zipWith (\b d -> b - tax * d) bias deltasAtual
    novosPesos = zipWith (\listaPesos d ->  zipWith (\w a -> w - tax * d * a) 
                          listaPesos actsAnterior) pesos deltasAtual


-- | Função auxiliar para calcular o delta de um neurónio interno.
innerDelta :: [Double] -> [Double] -> [[Double]] ->  [Double ]
innerDelta act deltas pesos = 
             zipWith (*) innerSum ( map sigmoid' act)
             where 
                innerSum = multMatrix (transpose pesos) deltas


-- | FUNÇÕES AUXILIARES DA ETAPA 1

-- | Função de ativação de sigmoid
sigmoid :: Double -> Double
sigmoid x = 1/(1 + exp (-x))

-- | Derivada da função de ativação de sigmoid
-- | Onde x corresponde a sigmoid x
sigmoid' :: Double -> Double
sigmoid' x = x * (1 - x)

-- | Função auxiliar feita na primeira etapa que transpõe uma matriz
transpose :: [[a]] -> [[a]]
transpose xs = [ [n !! y | n <- xs] | y <- [0..(length (head xs) - 1)] ]

-- | Função auxiliar feita na primeira etapa que multiplica uma matriz por um vetor
multMatrix :: Num a => [[a]] -> [a] -> [a]
multMatrix m v = [ produtoEscalarAux v x | x <- m ]

-- | Função auxiliar a multMatrix
-- | Calcula o produto escalar entre uma linha da matriz e o vetor
produtoEscalarAux :: Num a => [a] -> [a] -> a
produtoEscalarAux x y = sum [ (x !! i) * (y !! i) | i <- [0 .. length x - 1] ]

-- | Função auxiliar feita na primeira etapa do projeto que divide uma lista em grupos de tamanho n
chunksOf :: Int -> [a] -> [[a]]
chunksOf n xs = [ take n (drop y xs) | y <- [0, n..(length xs - 1)] ] 

