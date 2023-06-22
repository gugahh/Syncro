package gustavo.synchro;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class SynchroApp {

	ArrayList<Subtitle> objetosLegenda;
	ArrayList<String> arquivoOriginal;
	ArrayList<Integer> posIndicesLegendas;
	private boolean fazerBackupLegenda;
	private File arquivoBackup;
	private static final String howToGetHelpStr;

	static {
		howToGetHelpStr = "Digite java -jar syncro.jar -help para obter ajuda.";
	}

	public SynchroApp(){
		objetosLegenda = new ArrayList<Subtitle>();
		arquivoOriginal = new ArrayList<String>();
		posIndicesLegendas = new ArrayList<Integer>();
		fazerBackupLegenda = true;
	}

	/* Utilizado para testar a consist�ncia dos par�metros enviados,
	 * Quando a op��o selecionada for AJUSTAR o tempo das legendas.
	 * Qualquer inconsist�ncia far� o aplicativo dar exit(). */
	private static void testaParamsEntradaAdjust(String fileName, String tempoAjuste, String indiceLegendaInicial) {

		// Testando a consist�ncia do arquivo de legenda enviado.
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
		 * este dever� ser um inteiro positivo */
		if(indiceLegendaInicial!=null){
			try {
				int temp = Integer.parseInt( indiceLegendaInicial );
				// Se uma excess�o N�O foi lan�ada, o int � v�lido.
				// Verificando de � v�lido (>= 1).
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

	/* Utilizado para testar a consist�ncia dos par�metros enviados,
	 * Quando a op��o selecionada for RENUMERAR legendas.
	 * Qualquer inconsist�ncia far� o aplicativo dar exit(). */
	private static void testaParamsEntradaRenum(String fileName, String indiceLegendaInic, String indiceLegendaDesejada) {

		// Testando a consist�ncia do arquivo de legenda enviado.
		File arquivoLegenda = new File(fileName);
		if(!arquivoLegenda.exists()){
			System.out.println("\tErro: O arquivo solicitado nao existe.");
			System.out.println(howToGetHelpStr);
			System.exit(0);
		}

		//Verificando se os �ndices de legendas informados s�o inteiros.
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
	 * Lan�a uma NumberFormatException caso n�o consiga converter.]
	 * Ele pode possuir qualquer uma das seguintes m�scaras:
	 * 01:10s, +01:10s, -15:10,012, -1,103, 0,003
	 * (sinais s�o sempre aceitos,e o sinal '+' � sempre opcional).
	 * */
	private static int getMillisFromUserString(String tempo) throws NumberFormatException {
		int intSinal = 1;
		if(tempo.charAt(0)=='-' || tempo.charAt(0)=='+') { //Um sinal foi passado: (-) ou (+) (opcional)
			if(tempo.charAt(0)=='-'){
				intSinal = -1;
			}
			tempo=tempo.substring(1, tempo.length()); //Excluindo o sinal da String
		}

		if(tempo.charAt(tempo.length()-1)=='s' || tempo.charAt(tempo.length()-1)=='S'){
			//Usu�rio utilizou a nota��o de segundos.
			tempo=tempo.substring(0, tempo.length()-1); //Excluindo o "S", de segundos.
			/* nesta nota��o, peda�os de tempo devem ser separados por
			 * dois pontos; v�rgulas N�O s�o permitidas. */
			String[] pedacos = tempo.split(":");

			//Testando os peda�os
			if(pedacos.length > 3){
				throw new NumberFormatException(); //No m�ximo � permitido hh:mm:ss.
			}

			int segundos = Integer.parseInt(pedacos[pedacos.length-1]) * 1000;
			if(segundos>59000) {
				System.out.println("Valor de segundos informado e invalido.");
				System.exit(0);
			}
			int tempoEmMillis = segundos;

			if(pedacos.length>1){ //minutos
				int minutos = Integer.parseInt(pedacos[pedacos.length-2]) * 1000 * 60;
				tempoEmMillis += minutos;
			}

			if(pedacos.length==3){ //horas
				tempoEmMillis += Integer.parseInt(pedacos[pedacos.length-3]) * 1000 * 3600;
			}
			return tempoEmMillis * intSinal;

		} else {
			System.out.println("Este formato numerico ainda nao esta implementado. Lamento.");
			System.exit(0);
		}
		return 0; //Com sorte nunca chegaremos aqui
	}


	/* 1� itera��o: carregando ArrayList com todas as linhas da legenda.
	 * Sistema gera uma c�pia backup do arquivo de legendas. */
	void readFromFile(String fileName) throws IOException{

		File arquivoLegenda = new File(fileName);

		LineNumberReader bfread = null;
		PrintWriter writer = null;
		String currentLine = null;

		try {
			bfread = new LineNumberReader(new FileReader(arquivoLegenda)); //Tentado abrir arquivo. Opera��o pode falhar.

			if(fazerBackupLegenda){
				arquivoBackup = new File("Backup_" + fileName.replace(".srt", "") + "_" + System.currentTimeMillis() + ".srt");
				writer = new PrintWriter(new FileWriter(arquivoBackup));
			}

			while( (currentLine=bfread.readLine()) != null ){ // Loop: Lendo todas as linhas do arquivo texto at� o fim.

				//Escrevendo c�pia backup
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

	/* 2� Itera��o:
	 * Descobre quais das linhas obtidas representam um �ndice (de legenda).
	 * Um �ndice deve vir SEMPRE imediatamente seguido de uma linha de timestamps, e
	 * deve ser logicamente num�rico. Satisfeitas estas condi��es, armazena-se a posi��o
	 * (N� de linha) que cont�m o �ndice). */
	void localizaIndicesLegendas(){
		if(arquivoOriginal.size() < 2) return; //nada a fazer

		String linhaAtual = null;
		for(int idx=1; idx<arquivoOriginal.size(); idx++){

			/* Procurando linhas com timestamps:
			 * Estas dever�o ter o formato "00:00:01,520 --> 00:00:03,541" */
			linhaAtual = arquivoOriginal.get(idx);
			if(linhaAtual.length()==29 && isFormatoLinhaTimeStamp(linhaAtual)) {
				//Linha atual � uma linha de timestamps.
				//Linha anterior, ent�o, DEVERIA ser uma linha de �ndices.
				try {
					Integer.parseInt( arquivoOriginal.get(idx-1) );
					// Se uma excess�o N�O foi lan�ada, bloco id + tempo � correto.
					//Adicionar a linha de �ndice ao array de posi��es.
					posIndicesLegendas.add(new Integer(idx-1));
					//System.out.println("Achou: " + arquivoOriginal.get(idx-1));
				} catch( NumberFormatException e){}
			}
		}
	}

	/* 3� itera��o:
	 * Cria um array de objetos Subtitle;
	 * J� testamos e sabemos quais linhas cont�m um �ndice, e quais
	 * linhas cont�m timestamps v�lidos. */
	void criaArraySubtitles() {
		for(int idx=0; idx < posIndicesLegendas.size(); idx++) {

			int indiceLegenda	= Integer.parseInt(arquivoOriginal.get( posIndicesLegendas.get(idx) ));
			String startTime	= arquivoOriginal.get( posIndicesLegendas.get(idx)+1).substring(0, 12);
			String endTime		= arquivoOriginal.get( posIndicesLegendas.get(idx)+1).substring(17, 29);

			Subtitle sub = new Subtitle(indiceLegenda, startTime, endTime);
			objetosLegenda.add(sub);

			/*	Para se obter o texto da legenda, h� que se obter a posi��o de in�cio do pr�ximo item legenda;
			 * Caso se esteja processando a �ltima legenda, pegar linhas at� o fim do arrayList. */
			int posicaoFinal = 0;
			if(idx < (posIndicesLegendas.size() -1)){ //Legenda N�O � a �ltima
				posicaoFinal = posIndicesLegendas.get(idx+1); //N� de linha onde inicia a pr�xima legenda.
			}
			else posicaoFinal = arquivoOriginal.size();

			//Adicionando todas as linhas (texto das legendas) entre uma legenda e outra.
			String tempString = null;
			for(int g=posIndicesLegendas.get(idx)+2; g < posicaoFinal; g++) {
				tempString = arquivoOriginal.get( g );
				if(g>posIndicesLegendas.get(idx)+2){
					sub.appendTexto("\r\n"); //linhas posteriores � primeira merecem um Newline antes...
				}
				sub.appendTexto( tempString );
			}
		}
	}

	/* Usado para atrasar (ou adiantar) TODAS as legendas
	 * pelo tempo definido como par�metro;
	 * Params: id da legenda a partir da qual fazer a altera��o;
	 * 			tempo desejado de ajuste (milisegundos). */
	void modifytime(int timeInMilis, int indice) {
		/* Teste: se o valor for negativo (adiantamento), a primeira
		 * legenda n�o poder� ficar antes do segundo zero. */
		if(timeInMilis < 0){
			Subtitle st = objetosLegenda.get(0);
			if(st.getId() >= indice && st.getStartTime() + timeInMilis < 0){
				System.out.println("Erro: a primeira legenda n�o pode ser adiantada para antes do segundo zero.");
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
		//Ser� usado para ajustar a legenda desejada e as subsequentes
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
			//N�o encontrei a legenda a ser renumerada. Avisar Usu. Sair.
			System.out.println("\tO indice de legenda informado (que se desejava modificar) n�o foi encontrado.");
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
					writer.println(); //Legendas s�o separadas por uma linha em branco.
				}
				writer.println(); //A legenda � fechada com uma linha em branco a mais.

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer!=null){
				writer.close();
			}
		}
	}

	/* Verifica se uma linha � uma linha de timeStamps.
	 * Formato: "00:00:01,520 --> 00:00:03,541" */
	private static boolean isFormatoLinhaTimeStamp(String linha){
		if(linha.length()!=29) return false;

		//Testando a hora de in�cio da legenda.
		try{
			Subtitle.convertSubtitleTimeStampStringToInt(linha.substring(0, 12));
		} catch(NumberFormatException e) {
			return false; // Se deu erro, desistir.
		}

		//Testando a hora de fim da legenda.
		try{
			Subtitle.convertSubtitleTimeStampStringToInt(linha.substring(17, 29));
		} catch(NumberFormatException e) {
			return false; // Se deu erro, desistir.
		}
		return true;
	}

	/* Permite determinar se ser� feito (ou n�o) o backup do arquivo de legenda.*/
	public void setFazerBackupLegenda(boolean b){
		this.fazerBackupLegenda = b;
	}

	/* Utilizado para debugar ArrayList de �ndices de linhas em que come�am legendas  */
	void printIndicesLegendas(){
		System.out.println("---------\nIndices das Legendas\n---------");
		Iterator i = posIndicesLegendas.iterator();
		while(i.hasNext()){
			System.out.println(i.next());
		}
	}

	/* Utilizado para debugar ArrayList de linhas lidas do arquivo de legenda */
	void printAllLines(){
		Iterator i = arquivoOriginal.iterator();
		while(i.hasNext()){
			System.out.println(i.next());
		}
	}

	/* Utilizado para debugar o ArrayList de todos os objetos sbutitle encontrados */
	void printAllSubtitleObjects(){
		Iterator i = objetosLegenda.iterator();
		while(i.hasNext()){
			System.out.println(i.next().toString());
		}
	}

	public static void main(String[] args) throws IOException {


		if(args.length==0){
			//Usu�rio n�o passou nenhum par�metro. Exibir help b�sico:
			SynchroHelp.printBasicHelp();
			System.exit(0);
		}

		if(args.length>0 && 
				(args[0].equalsIgnoreCase("-help") || 
						args[0].equalsIgnoreCase("-h") ||
						args[0].equalsIgnoreCase("/help") ||
						args[0].equalsIgnoreCase("/h"))
		   ){
			//Usu solicitou um help estendido.
			SynchroHelp.printExtendedHelp();
			System.exit(0);
		}


		if(args.length==1){ //Usu quer algum tipo de alteracao, mas nao informou arquivo
			System.out.println("\tO parametro arquivo (de legenda) e obrigatorio");
			System.out.println("\tpara realizar qualquer operacao.");
			System.out.println(howToGetHelpStr);
			System.exit(0);
		}

		if(args[0].equalsIgnoreCase("-adjust")){

			/* Usu solicitou ajustar legenda.
			 * Testando consit�ncia de par�metros de entrada */

			if(args.length==2){ //Usu informou arquivo, mas nao o tempo de ajuste
				System.out.println("\tO parametro tempo (de ajuste) e obrigatorio");
				System.out.println("\tpara realizar esta operacao.");
				System.out.println(howToGetHelpStr);
				System.exit(0);
			}

			if(args.length >= 3){
				/* N� de params e legal. Pode-se tentar comecar.
				 * No caso de qualquer m�todo falhar a opera��o
				 * (execu��o da App) deve ser abortada. */

				SynchroApp s = new SynchroApp();

				// args[0] args[1]   args[2]  args[3](opc)     args[3 ou 4] (opc)
				//[-adjust [arquivo] [tempo] [indiceLegenda] ] [-nobak]

				String indiceLegendaInicial = null; //default caso usu�rio n�o passe uma refer�ncia de �ndice

				if(args.length>=4){
					//Args[3] pode ser um �ndice de legenda (opc) ou -nobak.
					if(args[3].equalsIgnoreCase("-nobak")){ //Usu solicitou n�o fazer backup
						s.setFazerBackupLegenda(false);
					} else indiceLegendaInicial = args[3];
				}

				if(args.length==5){ //O quarto parametro S� PODE ser referente ao Backup.
					if(args[4].equalsIgnoreCase("-nobak")){ //Usu solicitou n�o fazer backup
						s.setFazerBackupLegenda(false);
					} else { //Par�metro inv�lido (deveria ser -nobak, ou n�o existir).
						System.out.println("\tParametro invalido / desconhecido.");
						System.out.println(howToGetHelpStr);
						System.exit(0);
					}
				}

				/* N� de par�metros passados e ordem � correta.
				 * Testando a consist�ncia de cada um dos par�metros. */
				testaParamsEntradaAdjust(args[1], args[2], indiceLegendaInicial); //Caso passe, tudo Ok.

				/* 1� itera��o: carregando ArrayList com todas as linhas da legenda.
				 * Este m�todo tamb�m cria uma c�pia backup do arquivo de legenda. */
				s.readFromFile( args[1]);

				/* 2� itera��o: procurando linhas que contenham �ndices de legendas. */
				s.localizaIndicesLegendas();

				/* 3� itera��o: cria objetos Subtitle a partir das linhas de �ndices obtidas e
				 * das linhas de legendas armazenadas anteriormente. */
				s.criaArraySubtitles();

				//Efetua altera��es de tempo solicitadas.
				int indicelegendaInt = 1;
				//caso o Usu tenha informado um valor v�lido de em qual legenda iniciar , utiliz�-lo.
				if(indiceLegendaInicial!= null) indicelegendaInt = Integer.parseInt(indiceLegendaInicial);
				s.modifytime(getMillisFromUserString(args[2]), indicelegendaInt);

				//Salva as altera��es no arquivo de origem.
				s.saveChangedSubtitleFile(args[1]);
			}
		} else {
			if(args[0].equalsIgnoreCase("-renum")) { //Usu�rio tenta renumerar legendas

				if(args.length<4){ //Num de params menor que o esperado.
					System.out.println("\tNumero de parametros incorreto");
					System.out.println("\tpara realizar esta operacao.");
					System.out.println(howToGetHelpStr);
					System.exit(0);
				}

				if(args.length >= 4){
					/* N� de params � legal. Pode-se tentar comecar.
					 * No caso de qualquer m�todo falhar a opera��o
					 * (execu��o da App) deve ser abortada. */

					SynchroApp s = new SynchroApp();

					// args[0] args[1]   args[2]         args[3]           args[4] (opc)
					//[-renum [arquivo] [indiceInicial] [renumerarPara] ] [-nobak]

					String indiceLegendaInicial = null; //default caso usu�rio n�o passe uma refer�ncia de �ndice

					if(args.length==5){
						//Args[4] s� pode ser -nobak.
						if(args[4].equalsIgnoreCase("-nobak")){ //Usu solicitou n�o fazer backup
							s.setFazerBackupLegenda(false);
						} else {
							//Par�metro inv�lido. Unica op��o � -nobak.
							System.out.println("\tparametro incorreto");
							System.out.println(howToGetHelpStr);
							System.exit(0);
						}
					}

					/* N� de par�metros passados e ordem � correta.
					 * Testando a consist�ncia de cada um dos par�metros. */
					testaParamsEntradaRenum(args[1], args[2], args[3]); //Caso passe, tudo Ok.

					/* 1� itera��o: carregando ArrayList com todas as linhas da legenda.
					 * Este m�todo tamb�m cria uma c�pia backup do arquivo de legenda. */
					s.readFromFile( args[1]);

					/* 2� itera��o: procurando linhas que contenham �ndices de legendas. */
					s.localizaIndicesLegendas();

					/* 3� itera��o: cria objetos Subtitle a partir das linhas de �ndices obtidas e
					 * das linhas de legendas armazenadas anteriormente. */
					s.criaArraySubtitles();

					s.modifyIndiceLegendas(args[2], args[3]);

					//Salva as altera��es no arquivo de origem.
					s.saveChangedSubtitleFile(args[1]);

				}//4 params
			} else {
				// Caso n�o se esteja ajustando tempo, num legenda OU solicitando help, param � invalido.
				System.out.println("Parametro Invalido.");
				System.out.println(howToGetHelpStr);
				System.exit(0);
			}
		} //-adjust
	}//main

}

