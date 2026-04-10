import java.lang.StringBuilder;

/**
 * A classe {@code SumdokuGrid} permite criar, manipular e consultar o estado de uma grelha Sumdoku
 * A segunda parte do projeto da unidade curricular de IP
 * 
 * @author Rafael Pedro fc63697@alunos.fc.ul.pt
 * @author Josué Dias fc63699@alunos.fc.ul.pt
 */
public class SumdokuGrid{

  private final int size;
  private final int [][] grid;

/**
 * Constrói uma grelha Sumdoku
 * 
 * @param size tamanho da grelha
 * @requires {@code  size >= 3 && size <= 9}
 */
  public SumdokuGrid (int size){

    this.size = size;
    grid = new int[size][size]; 

  }

/**
 * Preenche uma dada casa da grelha com o valor fornecido
 * 
 * @param r linha da casa
 * @param c coluna da casa
 * @param value valor para preencher
 * 
 * @requires {@code 1 <= r && r <= size() && c <= 1 && c <= size() && value <= 1 && value <= size()}
 * @ensures {@code isFilled(r,c) && value(r,c) == value}
 */
  public void fill(int r, int c, int value){
    
    grid[r-1][c-1] = value;

  }

  /**
   * Verifica se uma dada casa está preenchida
   * 
   * @param r linha da casa
   * @param c coluna da casa
   * 
   * @return {@code true} se a casa na linha r e coluna c estiver preenchida. {@code false} caso contrário
   * @requires {@code 1 <= r && r <= size() && c <= 1 && c <= size()}
   */
  public boolean isFilled(int r, int c){

    return grid[r-1][c-1] > 0;
  }

  /**
   * O tamanho da grelha Sumdoku
   * 
   * @return tamanho da grelha sumdoku
   * @ensures{@code \result >= 3 && \result <= 9}
   */
  public int size(){
    
    return size;
  }

  /**
   * Uma representação textual da grelha Sumdoku. Espaços vazios são dados por "*".
   * 
   * @return representação textual da grelha
   */
  public String toString(){
    StringBuilder sb = new StringBuilder();

  for(int i = 0; i < size(); i++){

    for(int j = 0; j < size(); j++){

        if(grid[i][j] > 0){

            sb.append(grid[i][j]).append(" ");
        }
        else{

           sb.append(".").append(" ");
        }

    }
    sb.append("\n");
  }
  return sb.toString();
  }

  /**
   * O valor numa dada casa da grelha Sumdoku
   * 
   * @param r linha da casa
   * @param c coluna da casa
   * 
   * @return valor da casa na linha r e coluna c
   * @requires {@code 1 <= r && r <= size() && c <= 1 && c <= size() && isFilled(r,c)}
   * @ensures {@code 1 <= \result && \result <= size()}
   */
  public int value(int r, int c){

    return grid[r-1][c-1];
  }
}