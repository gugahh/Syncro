package gustavo.syncro.help;

import java.util.ArrayList;
import java.util.List;

public class BasicHelp extends AbstractHelp {

    private final List<String> listaTxt = new ArrayList<>();

    public BasicHelp() {
        addLinha("");
        addLinha("**************************************************************");
        addLinha("**  Syncro App. Um aplicativo para ajuste de legendas .SRT  **");
        addLinha("**   (c) 2007, 2025 Gustavo Santos (gugahh.br@gmail.com)    **");
        addLinha("**             Esse aplicativo e FREEWARE!                  **");
        addLinha("**************************************************************");
        addLinha("");
        addLinha("   Um aplicativo que fornece vários os modos de ajuste de legendas.");
        addLinha("   O modo mais utilizado é o que permite ajustar todas as legendas ");
        addLinha("   de um arquivo (adiantando ou atrasando todas elas), ");
        addLinha("   que é o modo -adjust. ");
        addLinha("");
        addLinha("digite \"java -jar syncro -help\" para obter um help completo. ");
        addLinha("");
    }

    @Override
    public List<String> getLinhasHelp() {
        return listaTxt;
    }
}
