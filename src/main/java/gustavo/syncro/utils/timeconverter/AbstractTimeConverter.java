package gustavo.syncro.utils.timeconverter;

import gustavo.syncro.exceptions.validacao.timestamp.TimestampInvalidoException;
import gustavo.syncro.exceptions.validacao.timestamp.TimestampNuloException;

public abstract class AbstractTimeConverter {

    /**
     * Verifica se a String informada pode ser convertida utilizando o Converter
     * atual (ou seja, eh o formato que este conversor esta apto a converter).
     * Deve utilizar REGEX, e so lancar excessao em caso de NULIDADE.
     * Note que o foco do metodo eh o formato correto, e não um tempo correto.
     * Um horario como 23:66:66 seria considerado valido pelo REGEX - e isso esta OK.
     *
     * @param txtCandidate texto a ser testado
     * @return true, caso a String possa ser processado por esse converter.
     * @throws TimestampNuloException  Lanca esta excecao no caso de um texto nulo.
     */
    public abstract boolean isAcceptedFormat(String txtCandidate)
        throws TimestampNuloException;

    /**
     * Transforma a sequencia de texto informacada no formato de timestamp
     * suportado em um valor de ajuste, em milisegundos
     *
     * @param txtSequence texto a ser convertido
     * @return valor em milisegundos
     * @throws TimestampInvalidoException caso seja encontrado um erro.
     */
    public abstract int getMillisFromString(String txtSequence)
            throws TimestampInvalidoException, TimestampNuloException;

    /**
     * Verifica se o valor informado eh nulo, ja lancando a excessao
     * adequada e mensagem
     * @param txtCandidate texto a ser testado
     * @throws TimestampNuloException
     */
    void validaTimeStampNulo(String txtCandidate)
            throws TimestampNuloException {
        if (txtCandidate == null) {
            throw new TimestampNuloException("O parametro informado para ajuste de tempo esta nulo.");
        }
    }
}
