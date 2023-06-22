package gustavo.synchro;

import java.io.IOException;
import java.io.InputStreamReader;

public class SynchroHelp {

	private static final StringBuilder basicHelp = new StringBuilder("");
	private static StringBuilder[] extendedHelp = new StringBuilder[4];

	static {

		//Definido Help Básico.
		basicHelp.append("-----------------------------------------------------------\n");
		basicHelp.append("|  Syncro - um aplicativo para sincronizacao de Legendas  |\n");
		basicHelp.append("|      (c) 2007, Gustavo Santos (gugahh@ig.com.br)        |\n");
		basicHelp.append("-----------------------------------------------------------\n");
		basicHelp.append("- Utilize-o para adiantar ou atrasar as legendas de um\n");
		basicHelp.append("  arquivo de legendas *.srt.\n");
		basicHelp.append("- Este software e Freeware. Se gostar, mande-me um\n");
		basicHelp.append("  cartao postal agradecendo (use a opcao -help para detalhes).\n");
		basicHelp.append("\n");
		basicHelp.append("Utilizacao:\n");
		basicHelp.append("-----------\n");
		basicHelp.append("\n");
		basicHelp.append("java -jar syncro.jar [-help]\n");
		basicHelp.append("java -jar syncro.jar [-adjust [arquivo] [tempo] [indiceLegenda] ] [-nobak]\n");
		basicHelp.append("\n");
		basicHelp.append("  [-help]:   exibe um help mais completo que este.\n");
		basicHelp.append("\n");
		basicHelp.append("  [-adjust]  Utilizada para ajustar tempo de legenda.\n");
		basicHelp.append("             Parametros arquivo, indiceLegenda e tempo sao obrigatorios.\n");
		basicHelp.append("\n");
		basicHelp.append("  [-nobak]: Opcional. Usar quando nao desejar criar um arquivo de backup.\n");

		//Definindo Help Avançado
		/*
		for(StringBuilder b: extendedHelp) {
			b = new StringBuilder(""); //Inicializando os stringBuilders
		} */
		extendedHelp[0] = new StringBuilder();
		extendedHelp[1] = new StringBuilder();
		extendedHelp[2] = new StringBuilder();
		extendedHelp[3] = new StringBuilder();

		//Tela 1:
		extendedHelp[0].append("\n");
		extendedHelp[0].append("-----------------------------------------------------------\n");
		extendedHelp[0].append("|  Syncro - um aplicativo para sincronizacao de Legendas  |\n");
		extendedHelp[0].append("|      (c) 2007, Gustavo Santos (gugahh@ig.com.br)        |\n");
		extendedHelp[0].append("-----------------------------------------------------------\n");
		extendedHelp[0].append("- Utilize-o para adiantar ou atrasar as legendas de um\n");
		extendedHelp[0].append("  arquivo de legendas *.srt.\n");
		extendedHelp[0].append("- Este software e Freeware. Se gostar, mande-me um\n");
		extendedHelp[0].append("  cartao postal agradecendo (use a opcao -help para detalhes).\n");
		extendedHelp[0].append("\n");
		extendedHelp[0].append("Utilizacao:\n");
		extendedHelp[0].append("-----------\n");
		extendedHelp[0].append("\n");
		extendedHelp[0].append("java -jar syncro.jar [-help]\n");
		extendedHelp[0].append("java -jar syncro.jar [-adjust [arquivo] [tempo] [indiceLegenda] ] [-nobak]\n");
		extendedHelp[0].append("\n");
		extendedHelp[0].append("  [-help]:   exibe esta tela de help.\n");
		extendedHelp[0].append("\n");
		extendedHelp[0].append("\n");
		extendedHelp[0].append("\n");
		extendedHelp[0].append("  -- Pressione enter (return) para continuar --\n");

		//Tela 2:
		extendedHelp[1].append("  [-adjust]  Utilizada para ajustar tempo de legenda.\n");
		extendedHelp[1].append("             Parametros arquivo, e tempo sao obrigatorios.\n");
		extendedHelp[1].append("\n");
		extendedHelp[1].append("       [arquivo]: Arquivo SRT que se deseja alterar. \n");
		extendedHelp[1].append("                  Nomes com caminho completo sao validos.\n");
		extendedHelp[1].append("\n");
		extendedHelp[1].append("       [tempo]: Tempo que se deseja que as legendas sejam adiantadas/atrasadas.\n");
		extendedHelp[1].append("		Pode ser positivo (atrasar) ou negativo (adiantar).\n");
		extendedHelp[1].append("                Para alterar minutos e segundos, utilize, por exemplo:\n");
		extendedHelp[1].append("\n");
		extendedHelp[1].append("                    01:10s     - para atrasar 1 minuto e dez segundos;\n");
		extendedHelp[1].append("                   +01:10s     - para atrasar 1 minuto e dez segundos\n");
		extendedHelp[1].append("                                 (o sinal positivo e opcional);\n");
		extendedHelp[1].append("                   -15:10,012  - para adiantar 15 minutos, 10 segundos e\n");
		extendedHelp[1].append("                                 12 milesimos.\n");
		extendedHelp[1].append("                   -1,103      - para adiantar 1 segundo e 103 milésimos.\n");
		extendedHelp[1].append("                    0,003      - para adiantar 3 milésimos de segundo.\n");
		extendedHelp[1].append("\n");
		extendedHelp[1].append("       [indiceLegenda]: (opcional) Legenda desde a qual se deseja alterar.\n");
		extendedHelp[1].append("                        Todas as legendas seguintes sofrerao a mesma alteracao\n");
		extendedHelp[1].append("                        automaticamente.\n");
		extendedHelp[1].append("                        Nao informar um valor de indice corresponde a alterar\n");
		extendedHelp[1].append("                        todas as legendas (e o mesmo que indice=1).\n");
		extendedHelp[1].append("\n");
		extendedHelp[1].append("  -- Pressione enter (return) para continuar --\n");

		//Tela 3:
		extendedHelp[2].append("\n");
		extendedHelp[2].append("       [-nobak]: Opcional. Define que voce nao deseja criar um arquivo de \n");
		extendedHelp[2].append("                 backup quando e feita a operacao de alteracao. O padrao e\n");
		extendedHelp[2].append("                 que um arquivo de backup seja criado.\n");
		extendedHelp[2].append("\n");
		extendedHelp[2].append("\n");
		extendedHelp[2].append("  Exemplo:\n");
		extendedHelp[2].append("  --------\n");
		extendedHelp[2].append("  * Para atrasar todas as legendas de heroes.srt em 1 minuto e 13 segundos:\n");
		extendedHelp[2].append("\n");
		extendedHelp[2].append("          java -jar syncro.jar -adjust heroes.srt 1:13s\n");
		extendedHelp[2].append("\n");
		extendedHelp[2].append("  * Para adiantar as legendas de heroes2.srt em 2 minutos 5 segundos \n");
		extendedHelp[2].append("    e 3 milesimos, a partir da 2a. legenda:\n");
		extendedHelp[2].append("\n");
		extendedHelp[2].append("          java -jar syncro.jar -adjust heroes2.srt -02:05,003 2\n");
		extendedHelp[2].append("\n");
		extendedHelp[2].append("  -- Pressione enter (return) para continuar --\n");
		extendedHelp[2].append("  \n");

		//Tela 4:
		extendedHelp[3].append("  \n");
		extendedHelp[3].append("  - Este software e um PostCardWare!\n");
		extendedHelp[3].append("    Se voce gostou dele, e este lhe foi util, mande um cartao postal para:\n");
		extendedHelp[3].append("	\n");
		extendedHelp[3].append("	------------------------------------------\n");
		extendedHelp[3].append("	Gustavo Santos\n");
		extendedHelp[3].append("	Rua Miguel de Frias, 164 apto 602\n");
		extendedHelp[3].append("	Icarai - Niteroi / RJ\n");
		extendedHelp[3].append("	CEP 24.220-003  BRAZIL\n");
		extendedHelp[3].append("	------------------------------------------\n");
		extendedHelp[3].append("	\n");
		extendedHelp[3].append("	Cartoes postais de qualquer lugar serao bem vindos e colecionados!\n");
		extendedHelp[3].append("	Não esqueca de adicionar o seu e-mail no cartao postal para que \n");
		extendedHelp[3].append("	eu possa agradecer.\n");
	}

	public static void printBasicHelp() {
		System.out.println(basicHelp);
	}

	public static void printExtendedHelp() throws IOException {
		int telaDeHelp = 0;
		InputStreamReader inp = new InputStreamReader(System.in);

		while(telaDeHelp < extendedHelp.length){
			System.out.print(extendedHelp[telaDeHelp]);
			inp.read(); //Aguarda input do usuário
			telaDeHelp++;
		}
	}
}
