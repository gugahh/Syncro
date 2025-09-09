package gustavo.syncro.exceptions.validacao;

public abstract class ValidacaoException extends Exception {

    public ValidacaoException() {
        super();
    }

    public ValidacaoException(String errorMessage) {
        super(errorMessage);
    }

    public ValidacaoException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
