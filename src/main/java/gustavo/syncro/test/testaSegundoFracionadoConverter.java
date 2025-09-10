package gustavo.syncro.test;

import gustavo.syncro.exceptions.validacao.timestamp.TimestampInvalidoException;
import gustavo.syncro.exceptions.validacao.timestamp.TimestampNuloException;
import gustavo.syncro.utils.timeconverter.AbstractTimeConverter;
import gustavo.syncro.utils.timeconverter.SegundoFracionadoConverter;

public class testaSegundoFracionadoConverter {

    // Positivos - Mascara +59s, +1s ou 15s
    static String[] testesPositivos_bloco1 = {
            "-01.0",
            "+59.1",
            "+00.9",
            "-00.0",
            "+00.0",
            "13.3",
            "-09.5",
            "-1.4"
    };

    // Negativos - Mascara +59s, +1s ou 15s
    static String[] testesNegativos_bloco1 = {
            "-01",
            "-01.8s",
            "01.8s",
            "-ef",
            "+ix",
            "-e",
            "+00.x",
            "+xx.9",
            "60.5",     //Tempo maximo eh 59.9s
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
