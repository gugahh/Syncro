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
        addLinha("*       OU apenas de uma legenda especifica em diante.          * ");
        addLinha("*****************************************************************");
        addLinha("");
        addLinha(" Nesse modo, os parametros nome de arquivo e tempo sao obrigatorios.");
        addLinha("");
        addLinha("java -jar syncro.jar [-adjust [arquivo] [tempo] [indiceLegenda] ] [-nobak]");
        addLinha("");
        addLinha("       [arquivo]: Arquivo SRT que se deseja alterar. ");
        addLinha("                  Nomes com caminho completo sao validos, mas nao podem ter espacos.");
        addLinha("");
        addLinha("       [tempo]: Tempo que se deseja que as legendas sejam adiantadas/atrasadas.");
        addLinha("		Pode ser positivo (atrasar) ou negativo (adiantar).");
        addLinha("                Para alterar minutos e segundos, utilize, por exemplo:");
        addLinha("");
        addLinha("                    01:10s     - para atrasar 1 minuto e dez segundos;");
        addLinha("                   +01:10s     - para atrasar 1 minuto e dez segundos");
        addLinha("                                 (o sinal positivo e opcional);");
        addLinha("");
        addLinha("                    1.5s       - para atrasar 1 segundo e meio (1 segundo e 500 milésimos).");
        addLinha("                    -31.8s     - para adiantar 31,8 segundos (31 segundo e 800 milésimos).");
        addLinha("");
        addLinha("       [indiceLegenda]: (opcional) Legenda desde a qual se deseja alterar.");
        addLinha("                        Todas as legendas seguintes sofrerao a mesma alteracao");
        addLinha("                        automaticamente.");
        addLinha("                        Nao informar um valor de indice corresponde a alterar");
        addLinha("                        todas as legendas (e o mesmo que indice=1).");
        addLinha("");
        addLinha("(continuacao - Modo [-adjust])");
        addLinha("       [-nobak]: Opcional. Define que voce nao deseja criar um arquivo de ");
        addLinha("                 backup quando e feita a operacao de alteracao. O padrao e");
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
        addLinha("    e 300 milesimos, a partir da 2a. legenda:");
        addLinha("");
        addLinha("          java -jar syncro.jar -adjust heroes2.srt -5.3s 2");
        addLinha("");
    }

    @Override
    public List<String> getLinhasHelp() {
        return listaTxt;
    }

}
