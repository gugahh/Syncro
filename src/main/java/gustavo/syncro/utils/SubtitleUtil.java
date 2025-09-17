package gustavo.syncro.utils;

import gustavo.syncro.Subtitle;
import gustavo.syncro.exceptions.FileReadException;
import gustavo.syncro.exceptions.validacao.ValidacaoException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe (singleton) responsavel por fazer as traducoes entre
 * String e o Subtitles (objeto de legenda).
 * Facilita os testes unitarios, pois os metodos nao sao mais estaticos.
 */
public class SubtitleUtil {

    /* implementacao do singleton */
    private static final SubtitleUtil instance = new SubtitleUtil();

    // Especifica o pattern 00:00:01,520
    private static final Pattern SUBTITLE_TS_PATTERN = Pattern.compile("\\d{2}:\\d{2}:\\d{2},\\d{3}");

    // private constructor to avoid client applications using the constructor
    private SubtitleUtil(){}

    public static SubtitleUtil getInstance() {
        return instance;
    }

        /**
     * Verifica se uma linha (de texto) é uma linha de timeStamps.
     * Formato: "00:00:01,520 --> 00:00:03,541"
     * @param linha a ser validada
     * */
    public boolean isFormatoLinhaTimeStamp(String linha){
        if(linha == null || linha.length() != 29) return false;

        //Testando a hora de início da legenda.
        boolean isInicioTimestamp = isStringSubtitleTimestamp(linha.substring(0, 12));

        //Testando a hora de fim da legenda.
        boolean isFinalTimestamp = isStringSubtitleTimestamp(linha.substring(17, 29));

        return isInicioTimestamp && isFinalTimestamp;
    }

    /**
     * Verifica se uma data String corresponde a um período no formato 00:00:01,520
     *
     * @param str string a ser testada
     * @return true, caso o texto esteja no formato correto.
     */
    public boolean isStringSubtitleTimestamp(String str) {
        if (str == null || str.length() != 12 ) { return false; }

        Matcher matcher = SUBTITLE_TS_PATTERN.matcher(str);
        return matcher.find(0);
    }

    /**
     * transforma uma String contendo um período no formato 00:00:01,520 para um int.
     *
     * @param subtitleMillisString string que se deseja convertar para misisegundos
     * @return valor da String convertido para millisegundos
     */
    public int convertSubtitleTimeStampStringToInt(String subtitleMillisString) {
        // System.out.println("subtitleMillisString. param: " + subtitleMillisString);
        int horas		= Integer.parseInt(subtitleMillisString.substring(0, 2));
        int minutos		= Integer.parseInt(subtitleMillisString.substring(3, 5));
        int segundos	= Integer.parseInt(subtitleMillisString.substring(6, 8));
        int milis		= Integer.parseInt(subtitleMillisString.substring(9, 12));

		/*
		System.out.println("Horas: " + horas);
		System.out.println("Minutos: " + minutos);
		System.out.println("Segundos: " + segundos);
		System.out.println("Milis: " + milis); */

        return ((horas*1000*3600)+(minutos*1000*60)+(segundos*1000)+milis);
    }

    //faz o inverso: transforma um inteiro em uma String contendo um período no formato 00:00:01,520
    public String convertIntToSubtitleTimeStamp(int timeStampAsInt){
        int horas		= (int)  timeStampAsInt / (1000*3600);
        int minutos		= (int) (timeStampAsInt % (1000*3600)) / 60000;
        int segundos	= (int) (timeStampAsInt % 60000) / 1000;
        int milis		= (int) timeStampAsInt % 1000;
        StringBuilder out = new StringBuilder();
        out.append(String.format("%02d", horas) + ":");
        out.append(String.format("%02d", minutos) + ":");
        out.append(String.format("%02d", segundos) + ",");
        out.append(String.format("%03d", milis));

        return out.toString();
    }

    /**
     * TODO:Esse deve ser o unico metodo publico da classe.
     * Recebe o NOME de um arquivo, e retorna uma lista de legendas a
     * partir deste. Havendo erros, retorna como excessoes.
     * @return lista de legendas
     */
    public List<Subtitle> obtemListaLegendasFromFile(String fileName)
            throws ValidacaoException, RuntimeException {

        List<String> arquivoOriginal = null;

        // Lista com cada uma das linhas do arquivo original, como texto.
        // Guarda as posicoes das Legendas (nos de linha) no texto original.
        List<Integer> posIndicesLegendas;

        // Lista com as Legendas, perfeitamente tratadas.
        List<Subtitle> objetosLegenda1;

        /* 1ª iteração: carregando ArrayList com todas as linhas da legenda.
         * Este método também cria uma cópia backup do arquivo de legenda. */


        try {
            FileReaderUtil fileReaderUtil = new FileReaderUtil();
            arquivoOriginal = fileReaderUtil.readFromFile(fileName);
        } catch (FileReadException e) {
            System.out.println(e.getMessage());
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /* 2ª Iteração:
         * Descobre quais das linhas obtidas representam um índice (de legenda).
         * Um índice deve vir SEMPRE imediatamente seguido de uma linha de timestamps, e
         * deve ser logicamente numérico. Satisfeitas estas condições, armazena-se a posição
         * (Nº de linha) que contém o índice). */
        posIndicesLegendas =  localizaIndicesLegendas(arquivoOriginal);

        /* 3ª iteração:
         * Cria um array de objetos Subtitle;
         * Já testamos e sabemos quais linhas contém um índice, e quais
         * linhas contém timestamps válidos. */
        objetosLegenda1 = criaArraySubtitles(arquivoOriginal, posIndicesLegendas);

        return objetosLegenda1;
    }

    /* 2ª Iteração:
     * Descobre quais das linhas obtidas representam um índice (de legenda).
     * Um índice deve vir SEMPRE imediatamente seguido de uma linha de timestamps, e
     * deve ser logicamente numérico. Satisfeitas estas condições, armazena-se a posição
     * (Nº de linha) que contém o índice). */
    private List<Integer> localizaIndicesLegendas(List<String> arquivoOriginal){
        // System.out.println(">> Entrou em localizaIndicesLegendas");

        List<Integer> posIndicesLegendas = new ArrayList<>();

        // if(arquivoOriginal.size() < 2) return; //nada a fazer

        String linhaAtual;
        for(int idx=1; idx < arquivoOriginal.size(); idx++){

            /* Procurando linhas com timestamps:
             * Estas deverão ter o formato "00:00:01,520 --> 00:00:03,541" */
            linhaAtual = arquivoOriginal.get(idx);

            if(linhaAtual.trim().isEmpty()) continue; // Linha em branco, ignorar.

            if(linhaAtual.length() == 29 && this.isFormatoLinhaTimeStamp(linhaAtual)) {
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
        return posIndicesLegendas;
    }

    /* 3ª iteração:
     * Cria um array de objetos Subtitle;
     * Já testamos e sabemos quais linhas contém um índice, e quais
     * linhas contém timestamps válidos. */
    List<Subtitle> criaArraySubtitles(List<String> arquivoOriginal, List<Integer> posIndicesLegendas) {

        List<Subtitle> objetosLegenda1 = new ArrayList<Subtitle>();

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

        return objetosLegenda1;
    }

}
