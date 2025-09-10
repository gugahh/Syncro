package gustavo.syncro.utils.timeconverter;

import gustavo.syncro.exceptions.validacao.timestamp.TimestampInvalidoException;
import gustavo.syncro.exceptions.validacao.timestamp.TimestampNuloException;

import java.util.regex.Pattern;

/**
 * Converte timestamps no formato (+01.5s / -01.5s) para milisegundos;
 * a parte fracionaria deve estar entre 0.0 e 0.9, e ela eh obrigatoria.
 * a parte inteira deve ser no máximo 59 segundos.
 */
public class SegundoFracionadoConverter extends AbstractTimeConverter {

    // Pattern +59.1 ou 59.2 (o sinal eh opcional).
    final Pattern DATE_PATTERN_1 = Pattern.compile( "^[+-]?[0-5]?[0-9].[0-9]$");

    final String MSG_MASCARA_INVALIDA = "O tempo de ajuste informado não esta na máscaras +00.5s ou 1.5s";

    @Override
    public boolean isAcceptedFormat(String txtCandidate) throws TimestampNuloException {
        validaTimeStampNulo(txtCandidate); // Lanca TimestampNuloException
        return DATE_PATTERN_1.matcher(txtCandidate).matches();
    }

    @Override
    public int getMillisFromString(String txtSequence) throws TimestampInvalidoException, TimestampNuloException {

        validaTimeStampNulo(txtSequence);   // lanca TimestampNuloException

        int millis = 0;

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

        /* nesta notação, a parte inteira e a fracionaria sao divididas por um ponto. */
        String[] pedacos = txtSequence.split("\\.");

        if (Integer.parseInt(pedacos[0]) != 0) {
            millis += Integer.parseInt(pedacos[0]) * 1000; // Evitando multiplicacao por zero, né
        }

        if (Integer.parseInt(pedacos[1]) != 0) {
            millis += Integer.parseInt(pedacos[0]) * 100; // Evitando multiplicacao por zero
        }

        return millis * intSinal;
    }
}
