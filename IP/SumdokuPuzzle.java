import java.lang.StringBuilder;
/**
 * A classe {@code SumdokuPuzzle} permite criar, manipular e consultar o estado de um puzzle Sumdoku.
 * A segunda parte do projeto da unidade curricular de IP
 */
public class SumdokuPuzzle{

    private int[][] groupMembership;
    private int[] groupsValues;

    /**
     * Constrói um puzzle Sumdoku
     * 
     * @param groupMembership matriz que define a associação de cada casa a um grupo
     * @param groupsValues vetor que define a soma de todas as casas que constituem um grupo
     * 
     * @requires {@code groupMembership != null && groupsValues != null}
     */
    public SumdokuPuzzle(int[][] groupMembership, int[] groupsValues){

        this.groupMembership = groupMembership;
        this.groupsValues = groupsValues;
    }

    /**
     * Verifica se o puzzle Sumdoku é um puzzle válido para jogar.
     * 
     * @param groupMembership matriz que define a associação de cada casa a um grupo
     * @param groupsValues vetor que define a soma de todas as casas que constituem um grupo
     * 
     * @return {@code true} se definir um puzzle válido {@code false} caso contrário
     * @ensures o puzzle é válido para jogar
     */
    public static boolean definesPuzzle(int[][] groupMembership, int[] groupsValues){

        int N = groupMembership.length;

        if( N < 3 || N  > 9){

            return false;
        }

        for(int i = 0; i < N; i++){

            if( groupMembership[0].length!= groupMembership[i].length){

                return false;
            }
        }


        if(groupsValues.length < 1 || groupsValues.length > (N*N)){

            return false;
        }

        int maxInterval = (N*N*N + N*N) / 2;
        for(int j = 0; j < groupsValues.length; j++){

            if(groupsValues[j] < 1 || groupsValues[j] > maxInterval ){

                return false;
            }

        }

        for(int k = 0; k < N; k++){

            for(int a = 0; a < N; a++){

            if(groupMembership[k][a] < 0 || groupMembership[k][a] > groupsValues.length-1){
                
                return false;
            }

            }
        }

        for(int g = 0; g < groupsValues.length; g++){

            boolean existsG = false;

            for(int l = 0; l < N; l++){

                for(int s = 0; s < N; s++){

                    if(groupMembership[l][s] == g){
                        existsG = true;
                        
                    }
                }

            }

            if(!existsG){

                return false;
            }
        }
        

       SumdokuSolver ss = new SumdokuSolver(groupMembership, groupsValues);
       
       if(ss.howManySolutions(2) != 1){

        return false;
       }
       
       return true;

    }

    /**
     * O tamanho do puzzle Sumdoku
     * 
     * @return tamanho do puzzle sumdoku
     * @ensures {@code \result >= 3 && \result <= 9}
     */
    public int size(){
        
        return groupMembership.length;
    }

    /**
     * O número de grupos do puzzle atual
     * 
     * @return número de grupos
     */
     public int numberOfGroups(){

      return groupsValues.length;
    }
    
    /**
     * O grupo ao qual uma dada casa pertence.
     * 
     * @param col coluna da casa
     * @param row linha da casa
     * 
     * @return o número do grupo
     */
    public int groupNumber(int col, int row){ 

        return groupMembership[row-1][col-1] + 1;
    }

    /**
     * A soma das casas de um dado grupo
     * 
     * @param group número do grupo
     * 
     * @return soma das casas do grupo
     * @requires {@code group >= 1 && group <= numberOfGroups()}
     */
    public int valueGroup(int group){ 

        return groupsValues[group-1];
    }
    
    /**
     * Verifica se a grelha jogada é a solução para o puzzle Sumdoku
     * 
     * @param playedGrid grelha jogada
     * 
     * @return {@code true} se a grelha for solução do puzzle {@code false} caso contrário
     * @requires {@code playedGrid != null}
     */
    public boolean isSolvedBy(SumdokuGrid playedGrid){ 

        if(playedGrid == null || playedGrid.size() != this.size()){
            
            return false;
        }

        for(int i = 1; i <= playedGrid.size(); i++){

            for(int k = 1; k <= playedGrid.size(); k++){

                if(!playedGrid.isFilled(k, i)){

                    return false;
                }
            }
        }

        SumdokuSolver ss = new SumdokuSolver(groupMembership,groupsValues);

        int [][][] solution = ss.findSolutions(1);

        for(int k = 1; k <= groupMembership.length; k++){

            for(int l = 1; l <= groupMembership.length; l++){

            if(solution[0][k-1][l-1] != playedGrid.value(k,l)){
                return false;
            }

            }
        }

        return true;
    }

    /**
     * Verifica se as casas preenchidas de uma grelha jogada são solução de um puzzle Sumdoku
     * 
     * @param playedGrid grelha jogada
     * 
     * @return {@code true} se as casas preenchidas correspondem com a solução {@code false} caso constrário
     * @requires {@code playedGrid.isFilled(r,c) && playedGrid != null}
     */
    public boolean isPartiallySolvedBy(SumdokuGrid playedGrid){ 

      if(playedGrid == null || playedGrid.size() != this.size()){
        
        return false;
      }

      SumdokuSolver ss = new SumdokuSolver(groupMembership,groupsValues);

        int [][][] solution = ss.findSolutions(1);

        if(solution.length == 0){

            return false;
        }

        int[][] s = solution[0];

        for(int k = 1; k <= this.size(); k++){

            for(int l = 1; l <= this.size(); l++){

                if(playedGrid.isFilled(l, k)){

                if(playedGrid.value(l,k) != s[k-1][l-1]){

                    return false;
                }

                }
            }
        }

      return true;
      
    }
    
    /**
     * A representação textual das pistas do puzzle Sumdoku
     * 
     * @return representação textual das pistas do puzzle
     */
    public String cluesToString(){ 
        

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < groupMembership.length; i++){

            for (int j = 0; j < groupMembership[i].length; j++){

            sb.append(" ").append(groupMembership[i][j] + 1);

            }
            sb.append("\n");
        }
          
        for(int k = 0; k < groupsValues.length; k++){

            sb.append("G").append(k+1).append(" ").append("=").append(" ").append(groupsValues[k]).append(" ");
        }
        sb.append("\n");

        return sb.toString();

    }

    /**
     * A representação textual do puzzle Sumdoku
     * 
     * @return representação textual do puzzle
     */
    public String toString(){ 
        
        StringBuilder sb = new StringBuilder();
       
        for (int i = 0; i < groupMembership.length; i++){

            for (int j = 0; j < groupMembership[i].length; j++){

            sb.append(" ").append(groupMembership[i][j] + 1);

            }
            sb.append("\n");
        }
        return sb.toString();
    }
}