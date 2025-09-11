package gustavo.syncro.test;

import gustavo.syncro.exceptions.validacao.timestamp.TimestampInvalidoException;
import gustavo.syncro.exceptions.validacao.timestamp.TimestampNuloException;
import gustavo.syncro.utils.timeconverter.AbstractTimeConverter;
import gustavo.syncro.utils.timeconverter.SegundoFracionadoConverter;

public class testaSegundoFracionadoConverter {

    // Positivos - Mascara +59s, +1s ou 15s
    static String[] testesPositivos_bloco1 = {
            "-01.0s",
            "+1.1s",
            "+59.1s",
            "+00.9s",
            "-00.0s",
            "+00.0s",
            "13.3s",
            "-09.5s",
            "-1.4s"
    };

    // Negativos - Mascara +59s, +1s ou 15s
    static String[] testesNegativos_bloco1 = {
            "-01",
            "-01.8",
            "01.8",
            "-efs",
            "+ixs",
            "-e0s",
            "+00.xs",
            "+xx.9s",
            "60.5s",     //Tempo maximo eh 59.9s
            ""
    };
    public static void main(String[] args) throws TimestampNuloException, TimestampInvalidoException {

        AbstractTimeConverter sfc = new SegundoFracionadoConverter();

        System.out.println(">> Testando isValidTimestamp - Mascara +59.1");
        System.out.println("Testes positivos");
        for (String item : testesPositivos_bloco1) {
            System.out.println("\tTestando: " + item + " :\t" + sfc.isAcceptedFormat(item));
        }

        System.out.println();
        System.out.println("Testes Negativos - Mascara +59.1");
        for (String item : testesNegativos_bloco1) {
            System.out.println("\tTestando: " + item + " :\t" + sfc.isAcceptedFormat(item));
        }

        System.out.println();
        System.out.println(">> Testando getMillisFromString - Mascara +59.1");
        for (String item : testesPositivos_bloco1) {
            System.out.println("\tTestando: " + item + " :\t" + sfc.getMillisFromString(item));
        }

    }

}
