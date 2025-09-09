package gustavo.syncro.actions;

import gustavo.syncro.Subtitle;
import gustavo.syncro.utils.HelpUtil;
import gustavo.syncro.utils.SubtitleUtil;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Classe responsavel por tudo o que for exclusivo a Renumerar Legendas
 */
public class RenumerarAction extends AbstractAction {

    /* implementacao do singleton */
    private static final RenumerarAction instance = new RenumerarAction();

    // private constructor to avoid client applications using the constructor
    private RenumerarAction(){}

    public static RenumerarAction getInstance() {
        return instance;
    }

    @Override
    public void doAction(String[] args) {

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

    /* Usado renumerar TODAS as legendas
     * iguais ou maiores que o indice (initialIndex) para
     * newIndex + sequencialDaLegenda. */
    public void modifyIndiceLegendas(List<Subtitle> objetosLegenda1,
                                      String initialIndex,
                                      String newIndex) {
        boolean valorAModificarFoiEncontrado = false;
        //Será usado para ajustar a legenda desejada e as subsequentes
        int intInitialIndex = Integer.parseInt(initialIndex);
        int modificador = Integer.parseInt(newIndex) - intInitialIndex;

        for(int i=0; i < objetosLegenda1.size(); i++){
            Subtitle st = objetosLegenda1.get(i);
            if(st.getId() >= intInitialIndex) {
                valorAModificarFoiEncontrado = true;
                st.setId(st.getId() + modificador);
            }
        }
        if(!valorAModificarFoiEncontrado){
            //Não encontrei a legenda a ser renumerada. Avisar Usu. Sair.
            System.out.println("\tO indice de legenda informado (que se desejava modificar) não foi encontrado.");
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(0);
        }
    }

}
