package gustavo.syncro.exceptions.validacao.timestamp;

import gustavo.syncro.exceptions.validacao.ValidacaoException;

public class TimestampNuloException extends ValidacaoException {

    public TimestampNuloException() {
        super();
    }

    public TimestampNuloException(String errorMessage) {
        super(errorMessage);
    }

    public TimestampNuloException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}
