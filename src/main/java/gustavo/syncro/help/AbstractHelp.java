package gustavo.syncro.help;

import java.util.List;

public abstract class AbstractHelp {

    public abstract List<String> getLinhasHelp();

    void addLinha(String texto) {
        getLinhasHelp().add(texto);
    }
}
