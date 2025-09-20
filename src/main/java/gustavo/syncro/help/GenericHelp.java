package gustavo.syncro.help;

import java.util.ArrayList;
import java.util.List;

public final class GenericHelp extends AbstractHelp {

    private final List<String> listaTxt = new ArrayList<>();

    public GenericHelp (){
        addLinha("**************************************************************");
        addLinha("**  Syncro App. Um aplicativo para ajuste de legendas .SRT  **");
        addLinha("**   (c) 2007, 2025 Gustavo Santos (gugahh.br@gmail.com)    **");
        addLinha("**             Esse aplicativo e FREEWARE!                  **");
        addLinha("**************************************************************");
        addLinha("");
        addLinha("   São vários os modos de ajuste de legendas.");
        addLinha("   O modo mais utilizado é o que permite ajustar todas as legendas ");
        addLinha("   de um arquivo (adiantando ou atrasando todas elas). ");
        addLinha("");
        addLinha("MODOS:");
        addLinha("");
    }

    @Override
    public List<String> getLinhasHelp() {
        return listaTxt;
    }
}
