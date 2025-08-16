package gustavo.syncro.exceptions;

public abstract class LegendaProcessException extends Exception {

    public LegendaProcessException() {
        super();
    }

    public LegendaProcessException(String errorMessage) {
        super(errorMessage);
    }

    public LegendaProcessException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
