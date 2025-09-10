package gustavo.syncro.utils.timeconverter;

import gustavo.syncro.exceptions.validacao.timestamp.TimestampInvalidoException;
import gustavo.syncro.exceptions.validacao.timestamp.TimestampNuloException;

import java.util.regex.Pattern;

/**
 * Converte timestamps no formato (+00:00:01s / -00:00:01s) para milisegundos;
 * Tambem aceita os formatos +00:01s, 00:01s, +01s, 01s, +1s e 1s.
 */
public class DataCompletaConverter extends AbstractTimeConverter {

    // Pattern +00:00:01s e 00:00:01s
    final Pattern DATE_PATTERN_1 = Pattern.compile( "^[+-]?\\d{2}:\\d{2}:\\d{2}[sS]$");

    // Patterns +00:01s e 00:01s
    final Pattern DATE_PATTERN_2 = Pattern.compile( "^[+-]?\\d{2}:\\d{2}[sS]$");

    // Pattern +01s, +1s e 1s
    final Pattern DATE_PATTERN_3 = Pattern.compile( "^[+-]?[0-9]{1,2}[sS]$");

    final String MSG_MASCARA_INVALIDA = "O tempo de ajuste informado não esta nas máscaras +00:00:01s, +00:01s, ou +01s";

    @Override
    public boolean isAcceptedFormat(String txtCandidate) throws TimestampNuloException {

        validaTimeStampNulo(txtCandidate); // Lanca TimestampNuloException

        // Essa classe trabalha com essas 3 mascaras:
        return DATE_PATTERN_1.matcher(txtCandidate).matches() ||
                DATE_PATTERN_2.matcher(txtCandidate).matches() ||
                DATE_PATTERN_3.matcher(txtCandidate).matches();
    }

    @Override
    public int getMillisFromString(String txtSequence)
            throws TimestampInvalidoException, TimestampNuloException {

        validaTimeStampNulo(txtSequence);   // lanca TimestampNuloException

        // Quem chama esse metodo tem que garantir que o texto usa as mascaras aceitas.
        // Esse erro nao deveria acontecer (erro de programacao).
        if (!this.isAcceptedFormat(txtSequence)) {
            throw new TimestampInvalidoException(MSG_MASCARA_INVALIDA);
        }

        int intSinal = 1;
        if(txtSequence.charAt(0)=='-' || txtSequence.charAt(0)=='+') { //Um sinal foi passado: (-) ou (+) (opcional)
            if(txtSequence.charAt(0)=='-'){
                intSinal = -1;
            }
            txtSequence = txtSequence.substring(1); //Excluindo o sinal da String
        }

        txtSequence=txtSequence.substring(0, txtSequence.length()-1); //Excluindo o "S", de segundos.

        /* nesta notação, pedaços de tempo devem ser separados por
         * dois pontos; vírgulas NÃO são permitidas. */
        String[] pedacos = txtSequence.split(":");

        //Testando os pedaços
        if(pedacos.length > 3){
            throw new TimestampInvalidoException(MSG_MASCARA_INVALIDA); //No máximo é permitido hh:mm:ss.
        }

        int segundos = Integer.parseInt(pedacos[pedacos.length-1]) * 1000;
        if(segundos > 59000) {
            throw new TimestampInvalidoException("Valor de segundos informado e invalido.");
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
    }
}
