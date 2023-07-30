package gustavo.syncro.utils;

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

}
