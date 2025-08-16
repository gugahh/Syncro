package gustavo.syncro.utils;

import java.util.regex.Pattern;

public class TimeConversionUtil {

    /* implementacao do singleton */
    private static final TimeConversionUtil instance = new TimeConversionUtil();

    // private constructor to avoid client applications using the constructor
    private TimeConversionUtil(){}

    public static TimeConversionUtil getInstance() {
        return instance;
    }

    /* Utilizado para converter uma String contendo o tempo para adiantar
     * ou atrasar as legendas para um valor em milisegundos;
     * Lança uma NumberFormatException caso não consiga converter.]
     * Ele pode possuir qualquer uma das seguintes máscaras:
     * 01:10s, +01:10s, -15:10,012, -1,103, 0,003
     * (sinais são sempre aceitos,e o sinal '+' é sempre opcional).
     * */
    public int getMillisFromUserString(String tempo) throws NumberFormatException {
        int intSinal = 1;
        if(tempo.charAt(0)=='-' || tempo.charAt(0)=='+') { //Um sinal foi passado: (-) ou (+) (opcional)
            if(tempo.charAt(0)=='-'){
                intSinal = -1;
            }
            tempo = tempo.substring(1, tempo.length()); //Excluindo o sinal da String
        }

        if(tempo.charAt(tempo.length()-1)=='s' || tempo.charAt(tempo.length()-1)=='S'){
            //Usuário utilizou a notação de segundos.
            tempo=tempo.substring(0, tempo.length()-1); //Excluindo o "S", de segundos.
            /* nesta notação, pedaços de tempo devem ser separados por
             * dois pontos; vírgulas NÃO são permitidas. */
            String[] pedacos = tempo.split(":");

            //Testando os pedaços
            if(pedacos.length > 3){
                throw new NumberFormatException(); //No máximo é permitido hh:mm:ss.
            }

            int segundos = Integer.parseInt(pedacos[pedacos.length-1]) * 1000;
            if(segundos > 59000) {
                System.out.println("Valor de segundos informado e invalido.");
                System.exit(0);
            }
            int tempoEmMillis = segundos;

            if(pedacos.length > 1){ //minutos
                int minutos = Integer.parseInt(pedacos[pedacos.length-2]) * 1000 * 60;
                tempoEmMillis += minutos;
            }

            if(pedacos.length == 3){ //horas
                tempoEmMillis += Integer.parseInt(pedacos[pedacos.length-3]) * 1000 * 3600;
            }
            return tempoEmMillis * intSinal;

        } else {
            System.out.println("Este formato numerico ainda nao esta implementado. Lamento.");
            System.exit(0);
        }
        return 0; //Com sorte nunca chegaremos aqui
    }

}
