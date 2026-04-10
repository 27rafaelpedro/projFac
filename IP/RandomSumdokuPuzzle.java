import java.util.Random;
/**
 * A classe {@code RandomSumdokuPuzzle} representa um gerador aleatório de puzzles Sumdoku.
 * Esta classe permite obter puzzles Sumdoku pré-definidos (built-in), de um determinado tamanho, numa ordem aleatória.
 * A segunda parte do projeto da unidade curricular de IP
 * 
 * @author Rafael Pedro fc63697@alunos.fc.ul.pt
 * @author Josué Dias fc63699@alunos.fc.ul.pt
 */
public class RandomSumdokuPuzzle{

    private int size;
    private SumdokuPuzzle[] availablePuzzles;
    private int puzzlesDone;
    
    /**
     * Constrói um gerador aleatório de puzzles Sumdoku built-in
     * 
     * @param size tamanho do puzzles que vão ser gerados
     * @requires {@code size >= 3 && size <= 9}
     */
    public RandomSumdokuPuzzle(int size){

      this.size = size;
      this.puzzlesDone = 0;
      makeBuiltInPuzzle();
      
    }

    /**
     * O tamanho do puzzle gerado
     * 
     * @return tamanho do puzzle gerado
     */
    public int size(){

        return size;
    }
    
    /**
     * Verifica se existe um próximo puzzle para gerar.
     * 
     * @return {@code true} se existir um próximo puzzle para gerar {@code false} caso contrário
     */
    public boolean hasNextPuzzle(){

    return puzzlesDone < availablePuzzles.length;
   }
   /**
    * Retorna o próximo puzzle disponível no gerador.
    *
    * @return o próximo puzzle do gerador
    * @requires {@code hasNextPuzzle()}
    * @ensures o puzzle seguinte é diferente do anterior
    */
    public SumdokuPuzzle nextPuzzle(){ 

        SumdokuPuzzle nextPuzzle = availablePuzzles[puzzlesDone];
        puzzlesDone++;

        return nextPuzzle;
    }
        /**
         * Método auxiliar para baralhar a ordem dos puzzles fornecidos pelo gerado
         * 
         * @param v vetor de puzzles
         * 
         * @requires {@code v != null}
         * @ensures distribuição aleatória dos elementos de {@code v}
         */
        private void shuffle(SumdokuPuzzle[] v){ 
         Random rd = new Random();
          for (int i = 0; i < v.length; i++){
            int r = i + rd.nextInt(v.length - i);
            SumdokuPuzzle temp = v[i];
            v[i] = v[r];
            v[r] = temp;
          }
        }

        /**
         * Método auxiliar para carregar puzzles pré-definidos de um dado tamanho no gerador
         * 
         * @requires {@code size == 5 || size == 3}
         */
        private void makeBuiltInPuzzle(){ 
            switch(size){

                case 3: 
                    availablePuzzles = new SumdokuPuzzle[2];
            
                    availablePuzzles[0] = new SumdokuPuzzle(new int[][] {{0, 0, 2}, {0, 1, 2}, {3, 3, 4}}, new int[] {5, 2, 5, 5, 1});
                    availablePuzzles[1] = new SumdokuPuzzle(new int[][] {{0, 0, 0}, {0, 0, 1}, {0, 1, 1}}, new int[] {14, 4});
            
                    shuffle(availablePuzzles);
                break;
                
                case 5:
                    availablePuzzles = new SumdokuPuzzle[1];
            
                    availablePuzzles[0] = new SumdokuPuzzle(new int[][] {{0, 0, 0, 1, 2}, {3, 3, 0, 1, 2}, {4, 5, 6, 6, 7}, {4, 5, 8, 8, 7}, {9, 9, 9, 10, 10}}, 
                    new int[] {14, 3, 5, 8, 5, 3, 9, 8, 5, 8, 7});
                break;
            
            }
        }
 }

