package gustavo.syncro.actions;

import gustavo.syncro.utils.HelpUtil;
import gustavo.syncro.utils.SubtitleUtil;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Classe responsavel por tudo o que for exclusivo a Renumerar Legendas
 */
public class RenumerarAction {

    /* implementacao do singleton */
    private static final RenumerarAction instance = new RenumerarAction();

    // private constructor to avoid client applications using the constructor
    private RenumerarAction(){}

    public static RenumerarAction getInstance() {
        return instance;
    }

    public void testaParamsEntradaRenum(String fileName,
                                        String indiceLegendaInic,
                                        String indiceLegendaDesejada) {

        // Testando a consistência do arquivo de legenda enviado.
        File arquivoLegenda = new File(fileName);
        if(!arquivoLegenda.exists()){
            System.out.println("\tErro: O arquivo solicitado nao existe.");
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(0);
        }

        //Verificando se os índices de legendas informados são inteiros.
        try {
            int inic = Integer.parseInt(indiceLegendaInic);
            int fim = Integer.parseInt(indiceLegendaDesejada);
            if(inic < 1 || fim < 1){
                System.out.println("\tErro: ambos os indice da legenda a ser modificada e o indice desejado ");
                System.out.println("\tdevem ser numeros inteiros positivos, e maiores que zero.");
                System.out.println(HelpUtil.howToGetHelpStr);
                System.exit(0);
            }
        } catch(NumberFormatException e) {
            System.out.println("\tErro: ambos os indice da legenda a ser modificada e o indice desejado devem ser numeros inteiros.");
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(0);
        }

    }

}
