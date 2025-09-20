package gustavo.syncro;

import gustavo.syncro.help.AbstractHelp;
import gustavo.syncro.help.BasicHelp;
import gustavo.syncro.help.GenericExtendedHelp;
import gustavo.syncro.help.TimeAdjustHelp;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SyncroHelp {

    private static final List<AbstractHelp> allHelpers = Arrays.asList(
            new GenericExtendedHelp(),
            new TimeAdjustHelp()
    );

	private static final StringBuilder[] extendedHelp =
        {
            new StringBuilder(),    // extendedHelp[0]
            new StringBuilder(),    // extendedHelp[1]
            new StringBuilder(),    // extendedHelp[2]
            new StringBuilder(),    // extendedHelp[3]
            new StringBuilder()     // extendedHelp[4]
        };

	static {

        // Help Estendido.
		//Tela 1:
		extendedHelp[0].append("\n");
		extendedHelp[0].append("------------------------------------------------------------\n");
		extendedHelp[0].append("|  Syncro - um aplicativo para sincronizacao de Legendas   |\n");
		extendedHelp[0].append("|      (c) 2007, 2025 Gustavo Santos (gugahh.br@gmail.com) |\n");
		extendedHelp[0].append("------------------------------------------------------------\n");

		extendedHelp[0].append("- Utilize-o para adiantar ou atrasar as legendas de um\n");
		extendedHelp[0].append("  arquivo de legendas *.srt.\n");
		extendedHelp[0].append("- Este software e Freeware. Se gostar, sinta-se à vontade\n");
		extendedHelp[0].append("  para enviar e-mail agradecendo.\n");
		extendedHelp[0].append("\n");
		extendedHelp[0].append("Utilizacao:\n");
		extendedHelp[0].append("-----------\n");
		extendedHelp[0].append("\n");
		extendedHelp[0].append("java -jar syncro.jar [-help]\n");
		extendedHelp[0].append("java -jar syncro.jar [-adjust [arquivo] [tempo] [indiceLegenda] ] [-nobak]\n");
        extendedHelp[0].append("java -jar syncro.jar [-renum [arquivo] [indiceorig] [indicenovo] ] [-nobak]\n");
		extendedHelp[0].append("\n");
		extendedHelp[0].append("  [-help]:   exibe esta tela de help.\n");

		//Tela 2:
		extendedHelp[1].append("** Modo [-adjust]  Utilizado para ajustar tempos de legendas.\n");
		extendedHelp[1].append("             Parametros arquivo e tempo sao obrigatorios.\n");
		extendedHelp[1].append("\n");
        extendedHelp[1].append("java -jar syncro.jar [-adjust [arquivo] [tempo] [indiceLegenda] ] [-nobak]\n");
        extendedHelp[1].append("\n");
		extendedHelp[1].append("       [arquivo]: Arquivo SRT que se deseja alterar. \n");
		extendedHelp[1].append("                  Nomes com caminho completo sao validos, mas nao podem ter espacos.\n");
		extendedHelp[1].append("\n");
		extendedHelp[1].append("       [tempo]: Tempo que se deseja que as legendas sejam adiantadas/atrasadas.\n");
		extendedHelp[1].append("		Pode ser positivo (atrasar) ou negativo (adiantar).\n");
		extendedHelp[1].append("                Para alterar minutos e segundos, utilize, por exemplo:\n");
		extendedHelp[1].append("\n");
		extendedHelp[1].append("                    01:10s     - para atrasar 1 minuto e dez segundos;\n");
		extendedHelp[1].append("                   +01:10s     - para atrasar 1 minuto e dez segundos\n");
		extendedHelp[1].append("                                 (o sinal positivo e opcional);\n");
        extendedHelp[1].append("\n");
        extendedHelp[1].append("                    1.5s       - para atrasar 1 segundo e meio (1 segundo e 500 milésimos).\n");
        extendedHelp[1].append("                    -31.8s     - para adiantar 31,8 segundos (31 segundo e 800 milésimos).\n");
		extendedHelp[1].append("\n");
		extendedHelp[1].append("       [indiceLegenda]: (opcional) Legenda desde a qual se deseja alterar.\n");
		extendedHelp[1].append("                        Todas as legendas seguintes sofrerao a mesma alteracao\n");
		extendedHelp[1].append("                        automaticamente.\n");
		extendedHelp[1].append("                        Nao informar um valor de indice corresponde a alterar\n");
		extendedHelp[1].append("                        todas as legendas (e o mesmo que indice=1).\n");

		//Tela 3:
        extendedHelp[2].append("\n(continuacao - Modo [-adjust])\n\n");
		extendedHelp[2].append("       [-nobak]: Opcional. Define que voce nao deseja criar um arquivo de \n");
		extendedHelp[2].append("                 backup quando e feita a operacao de alteracao. O padrao e\n");
		extendedHelp[2].append("                 que um arquivo de backup seja criado.\n");
		extendedHelp[2].append("\n");
		extendedHelp[2].append("\n");
		extendedHelp[2].append("  Exemplos:\n");
		extendedHelp[2].append("  --------\n");
		extendedHelp[2].append("  * Para atrasar todas as legendas de heroes.srt em 1 minuto e 13 segundos:\n");
		extendedHelp[2].append("\n");
		extendedHelp[2].append("          java -jar syncro.jar -adjust heroes.srt 1:13s\n");
		extendedHelp[2].append("\n");
		extendedHelp[2].append("  * Para adiantar as legendas de heroes2.srt em 5 segundos \n");
		extendedHelp[2].append("    e 300 milesimos, a partir da 2a. legenda:\n");
		extendedHelp[2].append("\n");
		extendedHelp[2].append("          java -jar syncro.jar -adjust heroes2.srt -5.3s 2\n");

        //Tela 4:
        extendedHelp[3].append("\n** Modo [-renum]   Utilizado para renumerar as legendas.\n");
        extendedHelp[3].append("             Parametros arquivo, indice da legenda original e \n");
        extendedHelp[3].append("             indice da legenda novo (desejado) sao obrigatorios. \n");
        extendedHelp[3].append("\n");
        extendedHelp[3].append("       java -jar syncro.jar [-renum [arquivo] [indiceorig] [indicenovo] ] [-nobak]\n");
        extendedHelp[3].append("\n");
        extendedHelp[3].append("       [arquivo]: Arquivo SRT que se deseja alterar. \n");
        extendedHelp[3].append("                  Nomes com caminho completo sao validos, mas nao podem ter espacos.\n");
        extendedHelp[3].append("\n");
        extendedHelp[3].append("       [indiceorig]: Legenda a partir da qual vamos renumerar.\n");
        extendedHelp[3].append("       [indicenovo]: Indice que se deseja para a legenda (e todas as seguintes).\n");
        extendedHelp[3].append("\n");
        extendedHelp[3].append("\n");
        extendedHelp[3].append("  Exemplo:\n");
        extendedHelp[3].append("  --------\n");
        extendedHelp[3].append("       java -jar syncro.jar -renum heroes.srt 1 3 [-nobak]\n\n");
        extendedHelp[3].append("           o comando acima irá renumerar todas as legendas a partir da\n");
        extendedHelp[3].append("           legenda 1. Esta se tornara a legenda 3,\n");
        extendedHelp[3].append("           a legenda 2 se tornara a 4, e assim por diante");
        extendedHelp[3].append("\n");

		//Tela 5:
		extendedHelp[4].append("  \n");
		extendedHelp[4].append("  - Este software e Freeware!\n");
		extendedHelp[4].append("    Espero que voce goste e que lhe seja util!\n");
		extendedHelp[4].append("	\n");
	}

	public static void printBasicHelp() {
        clearConsole();
        List<String> linhasHelp = new BasicHelp().getLinhasHelp();
        try {
            for (String umaLinha : linhasHelp) {
                System.out.println(umaLinha);
                Thread.sleep(10); // Espera 10ms
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Excessao inesperada em Thread.sleep.", e);
        }
        System.exit(0);
	}

	public static void printExtendedHelp()  {

        final int qtLinhasPorTela = 20; //TODO: deveria analisar o console primeiro.

        try {
            Scanner scanner = new Scanner(System.in);
            int contaHelpers = 0;

            for (AbstractHelp umHelper : allHelpers) {
                List<String> linhasTxtList = umHelper.getLinhasHelp();
                contaHelpers++;

                clearConsole(); // A cada bloco (helper), vamos limpar a tela.
                int countLinhasTxt = 0; // A cada helper, zera a contagem de linhas, pois limpamos a tela.

                for (String umaLinhaTxt : linhasTxtList) {
                    System.out.println(umaLinhaTxt);
                    Thread.sleep(8); // Espera 8ms a cada linha, pra efeito visual.

                    countLinhasTxt++;
                    if ((countLinhasTxt % qtLinhasPorTela == 0) // Atingiu o limite de linhas
                            || (countLinhasTxt == linhasTxtList.size() && // Chegou ao fim do helper atual
                            contaHelpers < allHelpers.size()    // E nao eh o ultimo helper.
                    )
                    ) {

                        //Atingiu o momento em que pedimos pro usuario apertar Enter pra continuar!
                        System.out.println("\n-- Digite ENTER para continuar, ou X para sair. --");
                        String opcao = scanner.nextLine();
                        if ("X".equalsIgnoreCase(opcao) ||
                                "EXIT".equalsIgnoreCase(opcao) ||
                                "QUIT".equalsIgnoreCase(opcao) ||
                                "Q".equalsIgnoreCase(opcao)) {
                            System.exit(0); // Usuario solicitou a saida.
                        }
                        clearConsole(); // Cada vez que aperta ENTER, limpamos a tela pra comecar do inicio da tela.
                    }
                } // Loop linhas de um helper.
            }
            scanner.close(); // Fechando o scanner pra liberar recursos.
        } catch (InterruptedException e) {
            throw new RuntimeException("Excessao inesperada em Thread.sleep.", e);
        }
	}

        /*
        // Versao velha. Espero poder remover.
        try {
            int contador = 0;
            for (StringBuilder sb : extendedHelp) {
                // clearConsole();
                System.out.print(sb);

                if (contador < extendedHelp.length - 1) {
                    System.out.print("\n  -- Pressione enter (return) para continuar --\n");
                    inp.read(); //Aguarda input do usuário. Menos no ultimo item.
                }
                contador++;
            }
         */

    /**
     * Limpa o console. Utiliza instrucoes especificas para cada S.O.
     */
    public static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            System.out.print("Excessao inesperada. Codigo -20003");
            System.exit(-1);
        }
    }

}
