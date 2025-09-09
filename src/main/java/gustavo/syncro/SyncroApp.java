package gustavo.syncro;

import gustavo.syncro.actions.RenumerarAction;
import gustavo.syncro.exceptions.FileReadException;
import gustavo.syncro.exceptions.ArquivoLegendaWriteException;
import gustavo.syncro.exceptions.PosicaoLegendaInvalidaException;
import gustavo.syncro.exceptions.validacao.ValidacaoException;
import gustavo.syncro.utils.*;
import gustavo.syncro.actions.TimeAdjustAction;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SyncroApp {

	private ArrayList<Subtitle> objetosLegenda1;
	private static ArrayList<String> arquivoOriginal;
	private ArrayList<Integer> posIndicesLegendas;
	private boolean fazerBackupLegenda;

	private static final SubtitleUtil sbtUtil = SubtitleUtil.getInstance();

	private static final TimeConversionUtil timeConversionUtil = TimeConversionUtil.getInstance();

	private static final TimeAdjustAction TIME_ADJUST_ACTION = TimeAdjustAction.getInstance();

	private static final RenumerarAction renumerarAction = RenumerarAction.getInstance();

	public SyncroApp(){
		objetosLegenda1 = new ArrayList<Subtitle>();
		arquivoOriginal = new ArrayList<>();
		posIndicesLegendas = new ArrayList<>();
		fazerBackupLegenda = true;
	}

	public List<Subtitle> getListaLegendas1() {
		return objetosLegenda1;
	}


	/* 2ª Iteração:
	 * Descobre quais das linhas obtidas representam um índice (de legenda).
	 * Um índice deve vir SEMPRE imediatamente seguido de uma linha de timestamps, e
	 * deve ser logicamente numérico. Satisfeitas estas condições, armazena-se a posição
	 * (Nº de linha) que contém o índice). */
	void localizaIndicesLegendas(){
		// System.out.println(">> Entrou em localizaIndicesLegendas");

		if(arquivoOriginal.size() < 2) return; //nada a fazer

		String linhaAtual;
		for(int idx=1; idx < arquivoOriginal.size(); idx++){

			/* Procurando linhas com timestamps:
			 * Estas deverão ter o formato "00:00:01,520 --> 00:00:03,541" */
			linhaAtual = arquivoOriginal.get(idx);

			if(linhaAtual.trim().isEmpty()) continue; // Linha em branco, ignorar.

			if(linhaAtual.length() == 29 && sbtUtil.isFormatoLinhaTimeStamp(linhaAtual)) {
				//Linha atual é uma linha de timestamps.
				//Linha anterior, então, DEVERIA ser uma linha de índices.
				try {
					Integer.parseInt( arquivoOriginal.get(idx-1) );
					// Se uma excessão NÃO foi lançada, bloco id + tempo é correto.
					//Adicionar a linha de índice ao array de posições.
					posIndicesLegendas.add(idx - 1);
					//System.out.println("Achou: " + arquivoOriginal.get(idx-1));
				} catch( NumberFormatException e){
					e.printStackTrace();
				}
			}
		}
		// System.out.println("<< Saiu de localizaIndicesLegendas");
	}

	/* 3ª iteração:
	 * Cria um array de objetos Subtitle;
	 * Já testamos e sabemos quais linhas contém um índice, e quais
	 * linhas contém timestamps válidos. */
	void criaArraySubtitles() {
		// System.out.println(">> Entrou em criaArraySubtitles");

		// System.out.println("posIndicesLegendas.size(): " + posIndicesLegendas.size());

		for(int idx=0; idx < posIndicesLegendas.size(); idx++) {

			// System.out.println("Processando idx :" + String.valueOf(idx));

			int indiceLegenda	= Integer.parseInt(arquivoOriginal.get( posIndicesLegendas.get(idx) ));
			String startTime	= arquivoOriginal.get( posIndicesLegendas.get(idx)+1).substring(0, 12);
			String endTime		= arquivoOriginal.get( posIndicesLegendas.get(idx)+1).substring(17, 29);

			Subtitle sub = new Subtitle(indiceLegenda, startTime, endTime);
			objetosLegenda1.add(sub);

			/*	Para se obter o texto da legenda, há que se obter a posição de início do próximo item legenda;
			 * Caso se esteja processando a última legenda, pegar linhas até o fim do arrayList. */
			int posicaoFinal;
			if(idx < (posIndicesLegendas.size() -1)){ //Legenda NÃO é a última
				posicaoFinal = posIndicesLegendas.get(idx+1); //Nº de linha onde inicia a próxima legenda.
			}
			else posicaoFinal = arquivoOriginal.size();

			//Adicionando todas as linhas (texto das legendas) entre uma legenda e outra.
			String tempString;
			for(int g=posIndicesLegendas.get(idx)+2; g < posicaoFinal; g++) {
				tempString = arquivoOriginal.get( g );
				if(g>posIndicesLegendas.get(idx)+2){
					sub.appendTexto("\r\n"); //linhas posteriores à primeira merecem um Newline antes...
				}
				sub.appendTexto( tempString );
			}
		}
		// printAllSubtitleObjects(); // Usado para Debugar.
		// System.out.println("<< Saiu de criaArraySubtitles");
	}

	/* Usado renumerar TODAS as legendas
	 * iguais ou maiores que o indice (initialIndex) para
	 * newIndex + sequencialDaLegenda. */
	// TODO: Mover para RenumerarAction
	private void modifyIndiceLegendas(String initialIndex, String newIndex) {
		boolean valorAModificarFoiEncontrado = false;
		//Será usado para ajustar a legenda desejada e as subsequentes
		int intInitialIndex = Integer.parseInt(initialIndex);
		int modificador = Integer.parseInt(newIndex) - intInitialIndex;

		for(int i=0; i < objetosLegenda1.size(); i++){
			Subtitle st = objetosLegenda1.get(i);
			if(st.getId() >= intInitialIndex) {
				valorAModificarFoiEncontrado = true;
				st.setId(st.getId() + modificador);
			}
		}
		if(!valorAModificarFoiEncontrado){
			//Não encontrei a legenda a ser renumerada. Avisar Usu. Sair.
			System.out.println("\tO indice de legenda informado (que se desejava modificar) não foi encontrado.");
			System.out.println(HelpUtil.howToGetHelpStr);
			System.exit(0);
		}
	}

	/* Permite determinar se será feito (ou não) o backup do arquivo de legenda.*/
	public void setFazerBackupLegenda(boolean b){
		this.fazerBackupLegenda = b;
	}

	public boolean getFazerBackupLegenda() { return this.fazerBackupLegenda; }

	/* Utilizado para debugar ArrayList de índices de linhas em que começam legendas  */
	void printIndicesLegendas(){
		System.out.println("---------\nIndices das Legendas\n---------");
		Iterator i = posIndicesLegendas.iterator();
		while(i.hasNext()){
			System.out.println(i.next());
		}
	}

	/* Utilizado para debugar ArrayList de linhas lidas do arquivo de legenda */
	void printAllLines(){
		Iterator<String> i = arquivoOriginal.iterator();
		while(i.hasNext()){
			System.out.println(i.next());
		}
	}

	/* Utilizado para debugar o ArrayList de todos os objetos subtitle encontrados */
	void printAllSubtitleObjects(){
		Iterator<Subtitle> i = objetosLegenda1.iterator();
		while(i.hasNext()){
			System.out.println(i.next().toString());
		}
	}

	public static void main(String[] args) throws IOException {

		if(args.length == 0){
			//Usuário não passou nenhum parâmetro. Exibir help básico:
			SyncroHelp.printBasicHelp();
			System.exit(0);
		}

		// TODO: Usar regex para simplificar abaixo.
		if(args.length > 0 &&
				(args[0].equalsIgnoreCase("-help") || 
						args[0].equalsIgnoreCase("-h") ||
						args[0].equalsIgnoreCase("/help") ||
						args[0].equalsIgnoreCase("/h"))
		   ){
			//Usu solicitou um help estendido.
			SyncroHelp.printExtendedHelp();
			System.exit(0);
		}

		if(args.length == 1){ //Usu quer algum tipo de alteracao, mas nao informou arquivo
			System.out.println("\tO parametro arquivo (de legenda) e obrigatorio");
			System.out.println("\tpara realizar qualquer operacao.");
			System.out.println(HelpUtil.howToGetHelpStr);
			System.exit(0);
		}

		if(args[0].equalsIgnoreCase("-adjust")){

			/* Usuario solicitou ajustar legenda.
			 * Testando consistência de parâmetros de entrada */

			if(args.length == 2){ //Usu informou arquivo, mas nao o tempo de ajuste
				System.out.println("\tO parametro tempo (de ajuste) e obrigatorio");
				System.out.println("\tpara realizar esta operacao.");
				System.out.println(HelpUtil.howToGetHelpStr);
				System.exit(0);
			}

			if(args.length >= 3){
				/* Nº de params e legal. Pode-se tentar comecar.
				 * No caso de qualquer método falhar a operação
				 * (execução da App) deve ser abortada. */

				SyncroApp s = new SyncroApp();

				// args[0] args[1]   args[2]  args[3](opc)     args[3 ou 4] (opc)
				//[-adjust [arquivo] [tempo] [indiceLegenda] ] [-nobak]

				String indiceLegendaInicial = null; //default caso usuário não passe uma referência de índice

				if(args.length >= 4){
					//Args[3] pode ser um índice de legenda (opc) ou -nobak.
					if(args[3].equalsIgnoreCase("-nobak") ||
							args[3].equalsIgnoreCase("-noback")){ //Usu solicitou não fazer backup
						s.setFazerBackupLegenda(false);
					} else indiceLegendaInicial = args[3];
				}

				if(args.length == 5){ //O quarto parametro SÓ PODE ser referente ao Backup.
					if(args[4].equalsIgnoreCase("-nobak")){ //Usu solicitou não fazer backup
						s.setFazerBackupLegenda(false);
					} else { //Parâmetro inválido (deveria ser -nobak, ou não existir).
						System.out.println("\tParametro invalido / desconhecido.");
						System.out.println(HelpUtil.howToGetHelpStr);
						System.exit(-1);
					}
				}

				/* Nº de parâmetros passados e ordem é correta.
				 * Testando a consistência de cada um dos parâmetros. */
				String msgTesteAjuste = TIME_ADJUST_ACTION.testaParamsEntradaAdjust(args[1], args[2], indiceLegendaInicial); //Caso passe, tudo Ok.
				if (msgTesteAjuste != null) {
					System.out.println(msgTesteAjuste);
					System.out.println(HelpUtil.howToGetHelpStr);
					System.exit(-1);
				}
				/* 1ª iteração: carregando ArrayList com todas as linhas da legenda.
				 * Este método também cria uma cópia backup do arquivo de legenda. */
				//s.readFromFile( args[1]);
                /*
				try {
					FileReaderUtil.readFromFile( args[1], arquivoOriginal, false);
				} catch (FileReadException e) {
					System.out.println(e.getMessage());
					System.out.println(HelpUtil.howToGetHelpStr);
					System.exit(-1);
				}
				*/

				/* 2ª iteração: procurando linhas que contenham índices de legendas. */
				// s.localizaIndicesLegendas();

				/* 3ª iteração: cria objetos Subtitle a partir das linhas de índices obtidas e
				 * das linhas de legendas armazenadas anteriormente. */
				// s.criaArraySubtitles();

                List<Subtitle> listaLegendas = null;
                try {
                    listaLegendas = sbtUtil.obtemListaLegendasFromFile(args[1]);
                } catch (ValidacaoException e) {
                    throw new RuntimeException(e);
                }

                if (null == listaLegendas || listaLegendas.isEmpty()) {
                    throw new RuntimeException("Lista de Legendas eh nula ou vazia.");
                }

                //Efetua alterações de tempo solicitadas.
				int indicelegendaInt = 1;
				//caso o Usu tenha informado um valor válido de em qual legenda iniciar, utilizá-lo.
				if(indiceLegendaInicial!= null) indicelegendaInt = Integer.parseInt(indiceLegendaInicial);

				try {
					TIME_ADJUST_ACTION.modificaTempoTodasLegendas(listaLegendas,
							timeConversionUtil.getMillisFromUserString(args[2]),
							indicelegendaInt
							);
					// s.modifytime(timeConversionUtil.getMillisFromUserString(args[2]), indicelegendaInt);
				} catch (PosicaoLegendaInvalidaException plie) {
					plie.printStackTrace();
					System.exit(-1);
				}

				//Salva as alterações no arquivo de origem.
				// s.saveChangedSubtitleFile(args[1]);
				try {
					SubtitleFileUtil.saveChangedSubtitleFile(args[1], listaLegendas);
				} catch (ArquivoLegendaWriteException e) {
					System.out.println(e.getMessage());
					System.out.println(HelpUtil.howToGetHelpStr);
					System.exit(-1);
				}

				System.out.println("O Tempo das Legendas foi ajustado com sucesso.");
			}
		} else {
			if(args[0].equalsIgnoreCase("-renum")) { //Usuário tenta renumerar legendas

				if(args.length<4){ //Num de params menor que o esperado.
					System.out.println("\tNumero de parametros incorreto");
					System.out.println("\tpara realizar esta operacao.");
					System.out.println(HelpUtil.howToGetHelpStr);
					System.exit(0);
				}

				if(args.length >= 4){
					/* Nº de params é legal. Pode-se tentar comecar.
					 * No caso de qualquer método falhar a operação
					 * (execução da App) deve ser abortada. */

					SyncroApp s = new SyncroApp();

					// args[0] args[1]   args[2]         args[3]           args[4] (opc)
					//[-renum [arquivo] [indiceInicial] [renumerarPara] ] [-nobak]

					String indiceLegendaInicial = null; //default caso usuário não passe uma referência de índice

					if(args.length == 5){
						//Args[4] só pode ser -nobak.
						if(args[4].equalsIgnoreCase("-nobak")){ //Usu solicitou não fazer backup
							s.setFazerBackupLegenda(false);
						} else {
							//Parâmetro inválido. Unica opção é -nobak.
							System.out.println("\tparametro incorreto");
							System.out.println(HelpUtil.howToGetHelpStr);
							System.exit(-1);
						}
					}

					/* Nº de parâmetros passados e ordem é correta.
					 * Testando a consistência de cada um dos parâmetros. */
					String retornoTestaRenumerar = renumerarAction.testaParamsEntradaRenum(args[1], args[2], args[3]);
					if (retornoTestaRenumerar != null) {
						System.out.println(retornoTestaRenumerar);
						System.out.println(HelpUtil.howToGetHelpStr);
						System.exit(-1);
					}


					/* 1ª iteração: carregando ArrayList com todas as linhas da legenda.
					 * Este método também cria uma cópia backup do arquivo de legenda. */
					// s.readFromFile( args[1]);
					try {
						FileReaderUtil.readFromFile( args[1], arquivoOriginal, false);
					} catch (FileReadException e) {
						System.out.println(e.getMessage());
						System.out.println(HelpUtil.howToGetHelpStr);
						System.exit(-1);
					}

					/* 2ª iteração: procurando linhas que contenham índices de legendas. */
					s.localizaIndicesLegendas();

					/* 3ª iteração: cria objetos Subtitle a partir das linhas de índices obtidas e
					 * das linhas de legendas armazenadas anteriormente. */
					s.criaArraySubtitles();

					s.modifyIndiceLegendas(args[2], args[3]);

					//Salva as alterações no arquivo de origem.
					// s.saveChangedSubtitleFile(args[1]);
					try {
						SubtitleFileUtil.saveChangedSubtitleFile(args[1], s.objetosLegenda1);
					} catch (ArquivoLegendaWriteException e) {
						System.out.println(e.getMessage());
						System.out.println(HelpUtil.howToGetHelpStr);
						System.exit(-1);
					}

					System.out.println("O Índice das Legendas foi ajustado com sucesso.");

				}//4 params
			} else {
				// Caso não se esteja ajustando tempo, num legenda OU solicitando help, param é invalido.
				System.out.println("Parametro Invalido.");
				System.out.println(HelpUtil.howToGetHelpStr);
				System.exit(0);
			}
		} //-adjust
	}//main

}
