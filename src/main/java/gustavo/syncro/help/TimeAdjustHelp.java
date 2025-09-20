package gustavo.syncro.help;

import java.util.ArrayList;
import java.util.List;

public final class TimeAdjustHelp extends AbstractHelp {

    private final List<String> listaTxt = new ArrayList<>();

    public TimeAdjustHelp() {
        addLinha("*****************************************************************");
        addLinha("*  Modo [-adjust]:                                              *");
        addLinha("*       Utilizado para ajustar tempos de legendas,              *");
        addLinha("*       adiantando ou atrasando todas as legendas do arquivo,   *");
        addLinha("*       OU apenas de uma legenda específica em diante.          * ");
        addLinha("*****************************************************************");
        addLinha("");
        addLinha(" Nesse modo, os parâmetros nome de arquivo e tempo sao obrigatórios.");
        addLinha(" Sintaxe:");
        addLinha("");
        addLinha("   java -jar syncro.jar [-adjust [arquivo] [tempo] [indiceLegenda] ] [-nobak]");
        addLinha("");
        addLinha("       [arquivo]: Arquivo SRT que se deseja alterar. ");
        addLinha("                  Nomes com caminho completo sao válidos, mas não podem ter espaços.");
        addLinha("");
        addLinha("       [tempo]: Tempo que se deseja que as legendas sejam adiantadas ou atrasadas.");
        addLinha("		Pode ser positivo (atrasar) ou negativo (adiantar).");
        addLinha("                Para alterar minutos e segundos, utilize, por exemplo:");
        addLinha("");
        addLinha("                    01:10s     - para atrasar 1 minuto e dez segundos;");
        addLinha("                   +01:10s     - para atrasar 1 minuto e dez segundos");
        addLinha("                                 (o sinal positivo é opcional);");
        addLinha("");
        addLinha("                    1.5s       - para atrasar 1 segundo e meio (1 segundo e 500 milésimos).");
        addLinha("                    -31.8s     - para adiantar 31 segundos e 800 milésimos.");
        addLinha("");
        addLinha("       [indiceLegenda]: (opcional) Legenda desde a qual se deseja alterar.");
        addLinha("                        Todas as legendas seguintes sofrerão a mesma alteracão");
        addLinha("                        automaticamente.");
        addLinha("                        Não informar um valor de índice corresponde a alterar");
        addLinha("                        todas as legendas (é o mesmo que indice = 1).");
        addLinha("");
        addLinha("       [-nobak]: Opcional. Define que você não deseja criar um arquivo de ");
        addLinha("                 backup quando é feita a operação de alteração. O padrão é");
        addLinha("                 que um arquivo de backup seja criado.");
        addLinha("");
        addLinha("  -------------");
        addLinha("  - Exemplos: -");
        addLinha("  -------------");
        addLinha("  * Para atrasar todas as legendas de heroes.srt em 1 minuto e 13 segundos:");
        addLinha("");
        addLinha("          java -jar syncro.jar -adjust heroes.srt 1:13s");
        addLinha("");
        addLinha("  * Para adiantar as legendas de heroes2.srt em 5 segundos ");
        addLinha("    e 300 milésimos, a partir da 2a. legenda:");
        addLinha("");
        addLinha("          java -jar syncro.jar -adjust heroes2.srt -5.3s 2");
        addLinha("");
    }

    @Override
    public List<String> getLinhasHelp() {
        return listaTxt;
    }

}
