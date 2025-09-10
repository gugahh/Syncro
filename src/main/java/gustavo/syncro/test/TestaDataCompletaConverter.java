package gustavo.syncro.test;

import gustavo.syncro.exceptions.validacao.timestamp.TimestampInvalidoException;
import gustavo.syncro.exceptions.validacao.timestamp.TimestampNuloException;
import gustavo.syncro.utils.timeconverter.DataCompletaConverter;

public class TestaDataCompletaConverter {

    static String[] testesPositivos = {
            "-00:00:01s",
            "+00:00:59s",
            "+00:01:00s",
            "+01:00:00s",
            "+10:20:35s",
            "-10:20:35s",
            "+13:59:10S"    // Segundos em caixa alta! Eh permitido.
    };

    static String[] testesNegativos = {
            "10:20:35s",
            "JUCA",
            "-00:00:01",
            "-ab:cd:ef",
            "15",
            "+00/00/59s",
            "+00-00-59s",
            "+00000159s"
    };

    public static void main(String[] args) throws TimestampNuloException, TimestampInvalidoException {

        DataCompletaConverter dcc = new DataCompletaConverter();

        System.out.println(">> Testando isValidTimestamp");
        System.out.println("Testes positivos");
        for (String item : testesPositivos) {
            System.out.println("\tTestando: " + item + " :\t" + dcc.isAcceptedFormat(item));
        }

        System.out.println();
        System.out.println("Testes Negativos");
        for (String item : testesNegativos) {
            System.out.println("\tTestando: " + item + " :\t" + dcc.isAcceptedFormat(item));
        }

        System.out.println();
        System.out.println(">> Testando getMillisFromString");
        for (String item : testesPositivos) {
            System.out.println("\tTestando: " + item + " :\t" + dcc.getMillisFromString(item));
        }
    }
}
