module Testes(
    prop_sizeUnaltered,
    prop_isNullNetwork,
    prop_isMseValid
) where

import Test.QuickCheck
import EAnteriores

-- | Teste quickcheck para verificar se uma network treinada mantem as propriedades da network pré-treino
prop_sizeUnaltered :: Network -> Bool
prop_sizeUnaltered org = length (layers org) == length (layers trained) && calcNetworkLength (layers org) == calcNetworkLength (layers trained)
   where trained = backPropagation 0.1 (head input) (head output) org
         input = [[0,0],[0,1],[1,0],[1,1]]
         output = [[0],[1],[1],[0]]

-- | Teste quickcheck para verificar se uma network é não vazia
prop_isNullNetwork :: Network -> Bool
prop_isNullNetwork net = not (null (layers net))

-- | Teste quickcheck para verificar se o erro mse final obtido é positivo
prop_isMseValid :: Network -> Bool
prop_isMseValid net = mse >= 0.0
    where trained = backPropagation 0.1 (head input) (head output) net
          input = [[0,0],[0,1],[1,0],[1,1]]
          output = [[0],[1],[1],[0]]
          predict = map(\inside -> last (forwardPass inside trained)) input
          mse = msePredictions predict output

calcNetworkLength :: [Layer] -> Int
calcNetworkLength [] = 0
calcNetworkLength (l:ls) = length (bias l) + calcMatrixLength (weight l) + calcNetworkLength ls

calcMatrixLength :: [[Double]] -> Int
calcMatrixLength [] = 0
calcMatrixLength weight = length weight * (length (head weight))

instance Arbitrary Layer where
    arbitrary = do
        
        nNeuronios <- choose(1, 4)
        nEntradas  <- choose(1, 4) 
        
        genLayer nNeuronios nEntradas

instance Arbitrary Network where
    arbitrary = genNetwork

genLayer:: Int -> Int -> Gen Layer
genLayer nNeuronios nEntradas = do
    pesos  <- vectorOf nNeuronios (vectorOf nEntradas (choose (0.0, 1.0)))
    bias <- vectorOf nNeuronios (choose (0.0, 1.0))
    return (Layer pesos bias)

genNetwork :: Gen Network
genNetwork = do
    l1 <- genLayer 4 2 
    l2 <- genLayer 1 4 
    
    return (Network [l1, l2])



