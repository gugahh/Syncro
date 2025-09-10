package gustavo.syncro.exceptions.validacao.timestamp;

import gustavo.syncro.exceptions.validacao.ValidacaoException;

public class TimestampInvalidoException extends Exception {

    public TimestampInvalidoException() {
        super();
    }

    public TimestampInvalidoException(String errorMessage) {
        super(errorMessage);
    }

    public TimestampInvalidoException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}
