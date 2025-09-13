package gustavo.syncro.actions;

/**
 * Essa action faz o oposto da Split Action:
 * Pega os vários pedacoes de um arquivo que foi explodido em partes,
 * e os re-combina num arquivo unico.
 */
public class ConcatAction extends AbstractAction {

    /* implementacao do singleton */
    private static final ConcatAction instance = new ConcatAction();

    private ConcatAction(){}

    public static ConcatAction getInstance() {
        return instance;
    }

    @Override
    public void doAction(String[] args) {

        System.out.println("\nSyncro App - executando ConcatAction\n");
        System.out.println("\nExecutado com SUCESSO.\n");
    }
}
