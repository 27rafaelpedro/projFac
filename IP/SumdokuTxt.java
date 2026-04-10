import java.util.Scanner;
/**
 * A classe {@code SumdokuTxt} permite ao utilizador jogar um jogo de Sumdoku.
 * A segunda parte do projeto da unidade curricular de IP
 * 
 * Compilar: javac SumdokuTxt.java
 * Executar: java SumdokuTxt (size)
 * 
 * @author Rafael Pedro fc63697@alunos.fc.ul.pt
 * @author Josué Dias fc63699@alunos.fc.ul.pt
 */
public class SumdokuTxt{
    public static void main(String[]args){
    
    Scanner reader = new Scanner(System.in);

    if(args.length > 0){

        int size = Integer.parseInt(args[0]);
        RandomSumdokuPuzzle rsp = new RandomSumdokuPuzzle(size);
        boolean continuePlay = true;

        do{
        SumdokuPuzzle puzzle = rsp.nextPuzzle();
        play(puzzle, size*size, reader);

        System.out.print("Queres jogar novamente? (true/false)? ");
        continuePlay = reader.nextBoolean();

        }while(rsp.hasNextPuzzle() && continuePlay);

        if(!continuePlay){
            System.out.print("Volta sempre jogador!");
        }
        else if(!rsp.hasNextPuzzle() && !continuePlay){
            System.out.print("Volta sempre jogador!");
        }
        else{
            System.out.print("Não existem mais puzzles deste tamanho.");
        }

        reader.close();
    }
    }
 /**
  * Permite ao utilizador jogar uma ou mais rondas de Sumdoku.

  * @param puzzle o puzzle em jogo
  * @param maxAttempts o número máximo de tentativas permitidas para resolver o puzzle
  * @param reader input do utilizador para efetuar as suas jogadas
  * 
  * @requires {@code puzzle != null && reader != null}
  */
public static void play(SumdokuPuzzle puzzle, int maxAttempts, Scanner reader){

        int attempts = 0;
        SumdokuGrid sg = new SumdokuGrid(puzzle.size());
        System.out.println("Bem-vindo novamente ao jogo do Sumdoku!");
        System.out.println("Neste jogo a grelha tem tamanho " + puzzle.size() + " e tens estas pistas para resolver o puzzle:");
        System.out.print(puzzle.cluesToString());
        System.out.println("Tens " + maxAttempts + " tentativas para resolver este puzzle. Bom jogo!");
        
          while(attempts < maxAttempts){
            int square = 0;
            int value = 0;
            attempts++;

            boolean validPlay = false;

            while(!validPlay){
                System.out.print("Casa a preencher? ");
                square = reader.nextInt();

                if(square >= 1 && square <= puzzle.size()*puzzle.size()){

                    validPlay = true;
                }
            
            else{
                System.out.println("Atenção! As casas são numeradas de 1 a " + puzzle.size()*puzzle.size() + ". Tenta novamente! ");
            }
            }
            validPlay = false;

            while(!validPlay){

                System.out.print("Valor a jogar? ");
                value = reader.nextInt();

                if(value >= 1 && value <= puzzle.size()){

                    validPlay = true;
                }
                else{
                    System.out.println("Atenção! Os valores a jogar estão numerados de 1 a " + puzzle.size() + ". Tenta novamente! ");
                }
            }
            
            int row = (square - 1)/puzzle.size() +1;
            int col = (square - 1)%puzzle.size() +1;
            int remainAttempts = (maxAttempts - attempts);
            
            System.out.println(" ");
            sg.fill(row,col,value);
            System.out.println(sg.toString());
            System.out.print("Restam-te " + remainAttempts + " tentativas. ");
         }

         if(puzzle.isSolvedBy(sg)){
            System.out.println("Parabéns! És o campeão do Sumdoku!");
         }
         else if(attempts == maxAttempts && !puzzle.isSolvedBy(sg)){
            System.out.println("Bom jogo! Tenta novamente!");
         }
}
}
