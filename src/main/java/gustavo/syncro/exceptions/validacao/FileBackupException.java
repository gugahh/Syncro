package gustavo.syncro.exceptions.validacao;

public class FileBackupException extends ValidacaoException {

    public FileBackupException() {
        super();
    }

    public FileBackupException(String errorMessage) {
        super(errorMessage);
    }

    public FileBackupException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
