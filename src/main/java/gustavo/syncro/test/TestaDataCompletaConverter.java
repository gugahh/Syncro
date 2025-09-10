package gustavo.syncro.test;

import gustavo.syncro.exceptions.validacao.timestamp.TimestampInvalidoException;
import gustavo.syncro.exceptions.validacao.timestamp.TimestampNuloException;
import gustavo.syncro.utils.timeconverter.DataCompletaConverter;

public class TestaDataCompletaConverter {

    // Mascara +00:00:59s
    static String[] testesPositivos_bloco1 = {
            "-00:00:01s",
            "+00:00:59s",
            "+00:01:00s",
            "+01:00:00s",
            "+10:20:35s",
            "-10:20:35s",
            "+13:59:10S",   // Segundos em caixa alta! Eh permitido.
            "00:59:10s"     // Eh permitido omitir o sinal.
    };

    // Mascara +00:00:59s
    static String[] testesNegativos_bloco1 = {
            "JUCA",
            "-00:00:01",
            "-ab:cd:ef",
            "15",
            "+00/00/59s",
            "+00-00-59s",
            "+00000159s"
    };

    // Positivos - Mascara +00:59s
    static String[] testesPositivos_bloco2 = {
            "-00:01s",
            "+00:59s",
            "+01:00s",
            "+00:00s",
            "+20:35s",
            "-20:35s",
            "+59:10S",    // Segundos em caixa alta! Eh permitido.
            "59:10s"      // Eh permitido omitir o sinal.
    };

    // Negativos - Mascara +00:59s
    static String[] testesNegativos_bloco2 = {
            "*20:35s",
            "-00:01",
            "00:03",
            "-cd:ef",
            "15",
            "+00/59s",
            "+00-59s",
            "+00059s",
            "+0159s"
    };

    // Positivos - Mascara +59s, +1s ou 15s
    static String[] testesPositivos_bloco3 = {
            "-01s",
            "+59s",
            "+00s",
            "-00s",
            "+1s",
            "-2s",
            "1s",
            "59s",
    };

    // Negativos - Mascara +59s, +1s ou 15s
    static String[] testesNegativos_bloco3 = {
            "-01",
            "-ef",
            "+ix",
            "-e",
            "+500s",
            "+1000s",
            ""
    };

    public static void main(String[] args) throws TimestampNuloException, TimestampInvalidoException {

        DataCompletaConverter dcc = new DataCompletaConverter();

        System.out.println(">> Testando isValidTimestamp - Mascara +00:00:59s");
        System.out.println("Testes positivos");
        for (String item : testesPositivos_bloco1) {
            System.out.println("\tTestando: " + item + " :\t" + dcc.isAcceptedFormat(item));
        }

        System.out.println();
        System.out.println("Testes Negativos - Mascara +00:00:59s");
        for (String item : testesNegativos_bloco1) {
            System.out.println("\tTestando: " + item + " :\t" + dcc.isAcceptedFormat(item));
        }

        System.out.println();
        System.out.println(">> Testando getMillisFromString - Mascara +00:00:59s");
        for (String item : testesPositivos_bloco1) {
            System.out.println("\tTestando: " + item + " :\t" + dcc.getMillisFromString(item));
        }

        System.out.println("\n------------------------------------");

        System.out.println(">> Testando isValidTimestamp - Mascara +00:59s");
        System.out.println("Testes positivos");
        for (String item : testesPositivos_bloco2) {
            System.out.println("\tTestando: " + item + " :\t" + dcc.isAcceptedFormat(item));
        }

        System.out.println();
        System.out.println("Testes Negativos - Mascara +00:59s");
        for (String item : testesNegativos_bloco2) {
            System.out.println("\tTestando: " + item + " :\t" + dcc.isAcceptedFormat(item));
        }

        System.out.println();
        System.out.println(">> Testando getMillisFromString - Mascara +00:59s");
        for (String item : testesPositivos_bloco2) {
            System.out.println("\tTestando: " + item + " :\t" + dcc.getMillisFromString(item));
        }

        System.out.println("\n------------------------------------");

        System.out.println(">> Testando isValidTimestamp - Mascara +59s");
        System.out.println("Testes positivos");
        for (String item : testesPositivos_bloco3) {
            System.out.println("\tTestando: " + item + " :\t" + dcc.isAcceptedFormat(item));
        }

        System.out.println();
        System.out.println("Testes Negativos - Mascara +59s");
        for (String item : testesNegativos_bloco3) {
            System.out.println("\tTestando: " + item + " :\t" + dcc.isAcceptedFormat(item));
        }

        System.out.println();
        System.out.println(">> Testando getMillisFromString - Mascara +59s");
        for (String item : testesPositivos_bloco3) {
            System.out.println("\tTestando: " + item + " :\t" + dcc.getMillisFromString(item));
        }
    }
}
