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

    /**
     * Testa se os parametros fornecidos sao adequados para Renumerar legendas;
     * Retorna uma mensagem de erro, no caso de qualquer nao conformidade;
     * Nulo significa que o ajuste pode, sim, ser realizado.
     * @param fileName nome do arquivo de legenda
     * @param indiceLegendaInic indice original da legenda inicial
     * @param indiceLegendaDesejada indice que se deseja para a legenda inicial informada
     * @return
     */
    public String testaParamsEntradaRenum(String fileName,
                                        String indiceLegendaInic,
                                        String indiceLegendaDesejada) {

        // Testando a consistência do arquivo de legenda enviado.
        File arquivoLegenda = new File(fileName);

        if(!arquivoLegenda.exists()){
            return "\tErro: O arquivo solicitado nao existe.";
            // System.out.println(HelpUtil.howToGetHelpStr);
        }

        //Verificando se os índices de legendas informados são inteiros.
        try {
            int inic = Integer.parseInt(indiceLegendaInic);
            int fim = Integer.parseInt(indiceLegendaDesejada);
            if(inic < 1 || fim < 1){
                return "\tErro: ambos os indice da legenda a ser modificada e o indice desejado " +
                "\tdevem ser numeros inteiros positivos, e maiores que zero.";
            }
        } catch(NumberFormatException e) {
            return "\tErro: ambos os indice da legenda a ser modificada e o indice desejado devem ser numeros inteiros.";
        }

        return null; // Sucesso.
    }

}
