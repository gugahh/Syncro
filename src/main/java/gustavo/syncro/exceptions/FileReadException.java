package gustavo.syncro.exceptions;

public class FileReadException extends Exception {

    public FileReadException() {
        super();
    }

    public FileReadException(String errorMessage) {
        super(errorMessage);
    }

    public FileReadException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
