package gustavo.syncro;

import gustavo.syncro.utils.SubtitleUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class SyncroApp {

	ArrayList<Subtitle> objetosLegenda;
	ArrayList<String> arquivoOriginal;
	ArrayList<Integer> posIndicesLegendas;
	private boolean fazerBackupLegenda;
	private File arquivoBackup;
	private static final String howToGetHelpStr;

	private static final SubtitleUtil sbtUtil = SubtitleUtil.getInstance();

	static {
		howToGetHelpStr = "Digite java -jar syncro.jar -help para obter ajuda.";
	}

	public SyncroApp(){
		objetosLegenda = new ArrayList<>();
		arquivoOriginal = new ArrayList<>();
		posIndicesLegendas = new ArrayList<>();
		fazerBackupLegenda = true;
	}

	/* Utilizado para testar a consistência dos parâmetros enviados,
	 * Quando a opção selecionada for AJUSTAR o tempo das legendas.
	 * Qualquer inconsistência fará o aplicativo dar exit(). */
	private static void testaParamsEntradaAdjust(String fileName, String tempoAjuste, String indiceLegendaInicial) {

		// Testando a consistência do arquivo de legenda enviado.
		File arquivoLegenda = new File(fileName);
		if(!arquivoLegenda.exists()){
			System.out.println("\tErro: O arquivo solicitado nao existe.");
			System.out.println(howToGetHelpStr);
			System.exit(0);
		}

		/* Testando o tempo de ajuste informado. */
		try {
			getMillisFromUserString(tempoAjuste);
		} catch(NumberFormatException e) {
			System.out.println("\tErro: O tempo para ajuste da legenda informado nao e valido..");
			System.out.println(howToGetHelpStr);
			System.exit(0);
		}

		/* Caso seja passado um valor de legenda inicial,
		 * este deverá ser um inteiro positivo */
		if(indiceLegendaInicial != null){
			try {
				int temp = Integer.parseInt( indiceLegendaInicial );
				// Se uma excessão NÃO foi lançada, o int é válido.
				// Verificando de é válido (>= 1).
				if(temp < 1){ //Erro
					System.out.println("\tErro: O indice da 1a. legenda a ser modificada deve ser\n\tum numero inteiro maior ou igual a 1.");
					System.out.println(howToGetHelpStr);
					System.exit(0);
				}
			} catch( NumberFormatException e){
				System.out.println("\tErro: O indice da 1a. legenda deve ser um numero inteiro.");
				System.out.println(howToGetHelpStr);
				System.exit(0);
			}
		}
	}

	/* Utilizado para testar a consistência dos parâmetros enviados,
	 * Quando a opção selecionada for RENUMERAR legendas.
	 * Qualquer inconsistência fará o aplicativo dar exit(). */
	private static void testaParamsEntradaRenum(String fileName, String indiceLegendaInic, String indiceLegendaDesejada) {

		// Testando a consistência do arquivo de legenda enviado.
		File arquivoLegenda = new File(fileName);
		if(!arquivoLegenda.exists()){
			System.out.println("\tErro: O arquivo solicitado nao existe.");
			System.out.println(howToGetHelpStr);
			System.exit(0);
		}

		//Verificando se os índices de legendas informados são inteiros.
		try {
			int inic = Integer.parseInt(indiceLegendaInic);
			int fim = Integer.parseInt(indiceLegendaDesejada);
			if(inic < 1 || fim < 1){
				System.out.println("\tErro: ambos os indice da legenda a ser modificada e o indice desejado ");
				System.out.println("\tdevem ser numeros inteiros positivos, e maiores que zero.");
				System.out.println(howToGetHelpStr);
				System.exit(0);
			}
		} catch(NumberFormatException e) {
			System.out.println("\tErro: ambos os indice da legenda a ser modificada e o indice desejado devem ser numeros inteiros.");
			System.out.println(howToGetHelpStr);
			System.exit(0);
		}

	}

	/* Utilizado para converter uma String contendo o tempo para adiantar
	 * ou atrasar as legendas para um valor em milisegundos;
	 * Lança uma NumberFormatException caso não consiga converter.]
	 * Ele pode possuir qualquer uma das seguintes máscaras:
	 * 01:10s, +01:10s, -15:10,012, -1,103, 0,003
	 * (sinais são sempre aceitos,e o sinal '+' é sempre opcional).
	 * */
	private static int getMillisFromUserString(String tempo) throws NumberFormatException {
		int intSinal = 1;
		if(tempo.charAt(0)=='-' || tempo.charAt(0)=='+') { //Um sinal foi passado: (-) ou (+) (opcional)
			if(tempo.charAt(0)=='-'){
				intSinal = -1;
			}
			tempo = tempo.substring(1, tempo.length()); //Excluindo o sinal da String
		}

		if(tempo.charAt(tempo.length()-1)=='s' || tempo.charAt(tempo.length()-1)=='S'){
			//Usuário utilizou a notação de segundos.
			tempo=tempo.substring(0, tempo.length()-1); //Excluindo o "S", de segundos.
			/* nesta notação, pedaços de tempo devem ser separados por
			 * dois pontos; vírgulas NÃO são permitidas. */
			String[] pedacos = tempo.split(":");

			//Testando os pedaços
			if(pedacos.length > 3){
				throw new NumberFormatException(); //No máximo é permitido hh:mm:ss.
			}

			int segundos = Integer.parseInt(pedacos[pedacos.length-1]) * 1000;
			if(segundos > 59000) {
				System.out.println("Valor de segundos informado e invalido.");
				System.exit(0);
			}
			int tempoEmMillis = segundos;

			if(pedacos.length > 1){ //minutos
				int minutos = Integer.parseInt(pedacos[pedacos.length-2]) * 1000 * 60;
				tempoEmMillis += minutos;
			}

			if(pedacos.length == 3){ //horas
				tempoEmMillis += Integer.parseInt(pedacos[pedacos.length-3]) * 1000 * 3600;
			}
			return tempoEmMillis * intSinal;

		} else {
			System.out.println("Este formato numerico ainda nao esta implementado. Lamento.");
			System.exit(0);
		}
		return 0; //Com sorte nunca chegaremos aqui
	}


	/* 1ª iteração: carregando ArrayList com todas as linhas da legenda.
	 * Sistema gera uma cópia backup do arquivo de legendas. */
	void readFromFile(String fileName) throws IOException{

		File arquivoLegenda = new File(fileName);

		LineNumberReader bfread = null;
		PrintWriter writer = null;
		String currentLine = null;

		try {
			bfread = new LineNumberReader(new FileReader(arquivoLegenda)); //Tentado abrir arquivo. Operação pode falhar.

			if(fazerBackupLegenda){
				arquivoBackup = new File("Backup_" + fileName.replace(".srt", "") + "_" + System.currentTimeMillis() + ".srt");
				writer = new PrintWriter(new FileWriter(arquivoBackup));
			}

			while( (currentLine=bfread.readLine()) != null ){ // Loop: Lendo todas as linhas do arquivo texto até o fim.

				//Escrevendo cópia backup
				if(fazerBackupLegenda){
					writer.println(currentLine);
				}

				//Carregando ArrayList
				currentLine = currentLine.trim();
				if(currentLine.length()>0){ //Evitando linhas em branco
					arquivoOriginal.add(currentLine);
				}
			}
		} finally {
			if(bfread!=null){
				bfread.close();
			}
			if(writer!=null){
				writer.close();
			}
		}
	}

	/* 2ª Iteração:
	 * Descobre quais das linhas obtidas representam um índice (de legenda).
	 * Um índice deve vir SEMPRE imediatamente seguido de uma linha de timestamps, e
	 * deve ser logicamente numérico. Satisfeitas estas condições, armazena-se a posição
	 * (Nº de linha) que contém o índice). */
	void localizaIndicesLegendas(){
		// System.out.println(">> Entrou em localizaIndicesLegendas");

		if(arquivoOriginal.size() < 2) return; //nada a fazer

		String linhaAtual = null;
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
				} catch( NumberFormatException e){}
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
			objetosLegenda.add(sub);

			/*	Para se obter o texto da legenda, há que se obter a posição de início do próximo item legenda;
			 * Caso se esteja processando a última legenda, pegar linhas até o fim do arrayList. */
			int posicaoFinal = 0;
			if(idx < (posIndicesLegendas.size() -1)){ //Legenda NÃO é a última
				posicaoFinal = posIndicesLegendas.get(idx+1); //Nº de linha onde inicia a próxima legenda.
			}
			else posicaoFinal = arquivoOriginal.size();

			//Adicionando todas as linhas (texto das legendas) entre uma legenda e outra.
			String tempString = null;
			for(int g=posIndicesLegendas.get(idx)+2; g < posicaoFinal; g++) {
				tempString = arquivoOriginal.get( g );
				if(g>posIndicesLegendas.get(idx)+2){
					sub.appendTexto("\r\n"); //linhas posteriores à primeira merecem um Newline antes...
				}
				sub.appendTexto( tempString );
			}
		}
		printAllSubtitleObjects();
		// System.out.println("<< Saiu de criaArraySubtitles");
	}

	/* Usado para atrasar (ou adiantar) TODAS as legendas
	 * pelo tempo definido como parâmetro;
	 * Params: id da legenda a partir da qual fazer a alteração;
	 * 			tempo desejado de ajuste (milisegundos). */
	void modifytime(int timeInMilis, int indice) {
		/* Teste: se o valor for negativo (adiantamento), a primeira
		 * legenda não poderá ficar antes do segundo zero. */
		if(timeInMilis < 0){
			Subtitle st = objetosLegenda.get(0);
			if(st.getId() >= indice && st.getStartTime() + timeInMilis < 0){
				System.out.println("Erro: a primeira legenda não pode ser adiantada para antes do segundo zero.");
				System.exit(0);
			}
		}

		for(int i=0; i < objetosLegenda.size(); i++){
			Subtitle st = objetosLegenda.get(i);
			if(st.getId() >= indice) {
				st.setStartTime(st.getStartTime() + timeInMilis);
				st.setEndTime(st.getEndTime() + timeInMilis);
			}
		}
	}

	/* Usado renumerar TODAS as legendas
	 * iguais ou maiores que o indice (initialIndex) para
	 * newIndex + sequencialDaLegenda. */
	private void modifyIndiceLegendas(String initialIndex, String newIndex) {
		boolean valorAModificarFoiEncontrado = false;
		//Será usado para ajustar a legenda desejada e as subsequentes
		int intInitialIndex = Integer.parseInt(initialIndex);
		int modificador = Integer.parseInt(newIndex) - intInitialIndex;

		for(int i=0; i < objetosLegenda.size(); i++){
			Subtitle st = objetosLegenda.get(i);
			if(st.getId() >= intInitialIndex) {
				valorAModificarFoiEncontrado = true;
				st.setId(st.getId() + modificador);
			}
		}
		if(!valorAModificarFoiEncontrado){
			//Não encontrei a legenda a ser renumerada. Avisar Usu. Sair.
			System.out.println("\tO indice de legenda informado (que se desejava modificar) não foi encontrado.");
			System.out.println(howToGetHelpStr);
			System.exit(0);
		}
	}


	/* Salva o arquivo de legendas novamente, SOBRE o arquivo de origem. */
	void saveChangedSubtitleFile(String fileName) {
		PrintWriter writer = null;

		try {
				writer = new PrintWriter(new FileWriter(fileName));

				for(Subtitle st : objetosLegenda){
					writer.println(st.getId());
					writer.print(st.getStartTimeAsString());
					writer.print(" --> ");
					writer.println(st.getEndTimeAsString());
					writer.println(st.getTexto());
					writer.println(); //Legendas são separadas por uma linha em branco.
				}
				writer.println(); //A legenda é fechada com uma linha em branco a mais.

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer!=null){
				writer.close();
			}
		}
	}

	/* Permite determinar se será feito (ou não) o backup do arquivo de legenda.*/
	public void setFazerBackupLegenda(boolean b){
		this.fazerBackupLegenda = b;
	}

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

	/* Utilizado para debugar o ArrayList de todos os objetos sbutitle encontrados */
	void printAllSubtitleObjects(){
		Iterator<Subtitle> i = objetosLegenda.iterator();
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
			System.out.println(howToGetHelpStr);
			System.exit(0);
		}

		if(args[0].equalsIgnoreCase("-adjust")){

			/* Usu solicitou ajustar legenda.
			 * Testando consitência de parâmetros de entrada */

			if(args.length == 2){ //Usu informou arquivo, mas nao o tempo de ajuste
				System.out.println("\tO parametro tempo (de ajuste) e obrigatorio");
				System.out.println("\tpara realizar esta operacao.");
				System.out.println(howToGetHelpStr);
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
					if(args[3].equalsIgnoreCase("-nobak")){ //Usu solicitou não fazer backup
						s.setFazerBackupLegenda(false);
					} else indiceLegendaInicial = args[3];
				}

				if(args.length == 5){ //O quarto parametro SÓ PODE ser referente ao Backup.
					if(args[4].equalsIgnoreCase("-nobak")){ //Usu solicitou não fazer backup
						s.setFazerBackupLegenda(false);
					} else { //Parâmetro inválido (deveria ser -nobak, ou não existir).
						System.out.println("\tParametro invalido / desconhecido.");
						System.out.println(howToGetHelpStr);
						System.exit(0);
					}
				}

				/* Nº de parâmetros passados e ordem é correta.
				 * Testando a consistência de cada um dos parâmetros. */
				testaParamsEntradaAdjust(args[1], args[2], indiceLegendaInicial); //Caso passe, tudo Ok.

				/* 1ª iteração: carregando ArrayList com todas as linhas da legenda.
				 * Este método também cria uma cópia backup do arquivo de legenda. */
				s.readFromFile( args[1]);

				/* 2ª iteração: procurando linhas que contenham índices de legendas. */
				s.localizaIndicesLegendas();

				/* 3ª iteração: cria objetos Subtitle a partir das linhas de índices obtidas e
				 * das linhas de legendas armazenadas anteriormente. */
				s.criaArraySubtitles();

				//Efetua alterações de tempo solicitadas.
				int indicelegendaInt = 1;
				//caso o Usu tenha informado um valor válido de em qual legenda iniciar , utilizá-lo.
				if(indiceLegendaInicial!= null) indicelegendaInt = Integer.parseInt(indiceLegendaInicial);
				s.modifytime(getMillisFromUserString(args[2]), indicelegendaInt);

				//Salva as alterações no arquivo de origem.
				s.saveChangedSubtitleFile(args[1]);
			}
		} else {
			if(args[0].equalsIgnoreCase("-renum")) { //Usuário tenta renumerar legendas

				if(args.length<4){ //Num de params menor que o esperado.
					System.out.println("\tNumero de parametros incorreto");
					System.out.println("\tpara realizar esta operacao.");
					System.out.println(howToGetHelpStr);
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
							System.out.println(howToGetHelpStr);
							System.exit(0);
						}
					}

					/* Nº de parâmetros passados e ordem é correta.
					 * Testando a consistência de cada um dos parâmetros. */
					testaParamsEntradaRenum(args[1], args[2], args[3]); //Caso passe, tudo Ok.

					/* 1ª iteração: carregando ArrayList com todas as linhas da legenda.
					 * Este método também cria uma cópia backup do arquivo de legenda. */
					s.readFromFile( args[1]);

					/* 2ª iteração: procurando linhas que contenham índices de legendas. */
					s.localizaIndicesLegendas();

					/* 3ª iteração: cria objetos Subtitle a partir das linhas de índices obtidas e
					 * das linhas de legendas armazenadas anteriormente. */
					s.criaArraySubtitles();

					s.modifyIndiceLegendas(args[2], args[3]);

					//Salva as alterações no arquivo de origem.
					s.saveChangedSubtitleFile(args[1]);

				}//4 params
			} else {
				// Caso não se esteja ajustando tempo, num legenda OU solicitando help, param é invalido.
				System.out.println("Parametro Invalido.");
				System.out.println(howToGetHelpStr);
				System.exit(0);
			}
		} //-adjust
	}//main

}
