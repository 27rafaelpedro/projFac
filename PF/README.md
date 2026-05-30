# Rede Neuronal em Haskell

Este projeto consiste no desenvolvimento de uma biblioteca para a construção, treino e avaliação de redes neuronais simples.

# Estrutura

O desenvolvimento do projeto encontra-se dividido em módulos para organização e gestão facilitada de código

1. **Operações de vetores e matrizes:** Implementação de operações de álgebra linear 
2. **Construção e propagação (Forward Pass):** Estruturação da rede através de tipos de dados algébricos (`Layer` e `Network`) e propagação dos dados através das camadas.
3. **Treino por backpropagation:** Treina ciclicamente a rede neuronal.
4. **Interface no terminal:** Escrita e leitura dos pesos em ficheiro de texto e modos de execução distintos.
5. **Testes de propriedades:** Validação de invariantes estruturais e funcionais usando a biblioteca `QuickCheck`.
