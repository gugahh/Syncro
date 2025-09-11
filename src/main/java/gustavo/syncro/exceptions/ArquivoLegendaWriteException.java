package gustavo.syncro.exceptions;

public class ArquivoLegendaWriteException extends Exception {

    public ArquivoLegendaWriteException() {
        super();
    }

    public ArquivoLegendaWriteException(String errorMessage) {
        super(errorMessage);
    }

    public ArquivoLegendaWriteException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
