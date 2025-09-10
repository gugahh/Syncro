package gustavo.syncro.utils.timeconverter;

import gustavo.syncro.exceptions.validacao.timestamp.TimestampInvalidoException;
import gustavo.syncro.exceptions.validacao.timestamp.TimestampNuloException;

import java.util.regex.Pattern;

/**
 * Converte datas o formato +00:00:01s / -00:00:01s para milisegundos.
 */
public class DataCompletaConverter extends AbstractTimeConverter {

    final Pattern DATE_PATTERN;

    final String MSM_MASCARA_INVALIDA = "O tempo de ajuste informado não esta na máscara +00:00:01s.";

    public DataCompletaConverter() {
        DATE_PATTERN = Pattern.compile( "^[+-]\\d{2}:\\d{2}:\\d{2}[sS]$");
    }

    @Override
    public boolean isAcceptedFormat(String txtCandidate) throws TimestampNuloException {

        validaTimeStampNulo(txtCandidate); // Lanca TimestampNuloException
        return DATE_PATTERN.matcher(txtCandidate).matches();
    }

    @Override
    public int getMillisFromString(String txtSequence)
            throws TimestampInvalidoException, TimestampNuloException {

        validaTimeStampNulo(txtSequence);   // lanca TimestampNuloException

        // Quem chama esse metodo tem que garantir que o texto esta na mascara +00:00:01s
        if (!this.isAcceptedFormat(txtSequence)) {
            throw new TimestampInvalidoException(MSM_MASCARA_INVALIDA);
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
            throw new TimestampInvalidoException(MSM_MASCARA_INVALIDA); //No máximo é permitido hh:mm:ss.
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
