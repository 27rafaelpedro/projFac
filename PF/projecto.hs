module Main where

import System.Environment
import Test.QuickCheck
import EAnteriores
import Testes

main :: IO ()
main = do
    args <- getArgs
 
    if head args == "--train"
            then do
                let outputFileName = args !! 1
                let inputs = [[0,0],[0,1],[1,0],[1,1]]
                let targets = [[0],[1],[1],[0]]
                let netInicial = buildNetwork 2 [4,1] (repeat 0.1)
                
                let netTreinada = training 100000 0.1 (inputs, targets) netInicial
                let previsoes = map (\i -> last (forwardPass i netTreinada)) inputs
                let erro = msePredictions previsoes targets
                
                writeFile outputFileName (show netTreinada)
                putStrLn $ "Pesos guardados em: " ++ outputFileName
                print (zip previsoes targets)
                putStrLn $ "MSE: " ++ show erro

        else if head args == "--predict"
            then do
                let inputFile = args !! 1
                conteudo <- readFile inputFile
                let net = read conteudo :: Network
                let inputs = [[0,0],[0,1],[1,0],[1,1]]
                print $ map (\i -> last (forwardPass i net)) inputs

        else if head args == "--test"
            then do
                quickCheck prop_sizeUnaltered
                quickCheck prop_isMseValid
                quickCheck prop_isNullNetwork

        else putStrLn "Invalid mode, try again!"

-- | Treina a rede durante n iterações, com uma taxa
-- | de aprendizagem e percorrendo os exemplos ciclicamente

training :: Int -> Double -> ([[Double]], [[Double]])-> Network -> Network
training n taxa (input , target )  net  = 

         foldl  aplicaBackProp net exemplos             
     where
        exemplos = take n (cycle (zip input target))

        aplicaBackProp  rede (entrada , esperado) = 
                     backPropagation taxa entrada esperado rede
