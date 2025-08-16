package gustavo.syncro.actions;

import gustavo.syncro.Subtitle;
import gustavo.syncro.exceptions.PosicaoLegendaInvalidaException;
import gustavo.syncro.utils.HelpUtil;
import gustavo.syncro.utils.TimeConversionUtil;

import java.io.File;
import java.util.List;

public class TimeAdjustAction {

    private static final TimeAdjustAction instance = new TimeAdjustAction();

    // private constructor to avoid client applications using the constructor
    public static TimeAdjustAction getInstance() {
        return instance;
    }

    private TimeAdjustAction(){}


    /**
     * Utilizado para testar a consistência dos parâmetros enviados,
     * Quando a opção selecionada for AJUSTAR o tempo das legendas.
     * Qualquer inconsistência fará o aplicativo dar exit().
     * @param fileName arquivo SRT a ser testado
     * @param tempoAjuste tempo de ajuste
     * @param indiceLegendaInicial indice da legenda inicial
     * @return o erro encontrado, ou null no caso de nao haver erros.
     */
    public String testaParamsEntradaAdjust(String fileName, String tempoAjuste, String indiceLegendaInicial) {

        // Testando a consistência do arquivo de legenda enviado.
        File arquivoLegenda = new File(fileName);
        if(!arquivoLegenda.exists()){
            return "\tErro: O arquivo solicitado nao existe.";
        }

        /* Testando o tempo de ajuste informado. */
        try {
            TimeConversionUtil.getInstance().getMillisFromUserString(tempoAjuste);
        } catch(NumberFormatException e) {
            return "\tErro: O tempo para ajuste da legenda informado nao e valido..";
        }

        /* Caso seja passado um valor de legenda inicial,
         * este deverá ser um inteiro positivo */
        if(indiceLegendaInicial != null){
            try {
                int temp = Integer.parseInt( indiceLegendaInicial );
                // Se uma excessão NÃO foi lançada, o int é válido.
                // Verificando de é válido (>= 1).
                if(temp < 1){ //Erro
                    return "\tErro: O indice da 1a. legenda a ser modificada deve ser\n\tum numero inteiro maior ou igual a 1.";
                }
            } catch( NumberFormatException e){
                return "\tErro: O indice da 1a. legenda deve ser um numero inteiro.";
            }
        }
        return null; // Zero erros encontrados.
    }

    /* Usado para atrasar (ou adiantar) TODAS as legendas
     * pelo tempo definido como parâmetro;
     * Params:
     * - Lista contendo as legendas que se deseja ajustar.
     * - tempo desejado de ajuste (milisegundos)
     * - id da legenda a partir da qual fazer a alteração
     * modifytime
     * 			 */
    public void modificaTempoTodasLegendas(
            List<Subtitle> objetosLegenda ,
            int timeInMilis,
            int indice) throws PosicaoLegendaInvalidaException {
        /* Teste: se o valor for negativo (adiantamento), a primeira
         * legenda não poderá ficar antes do segundo zero. */
        if(timeInMilis < 0){
            Subtitle st = objetosLegenda.get(0);
            if(st.getId() >= indice && st.getStartTime() + timeInMilis < 0){
                throw new PosicaoLegendaInvalidaException("Erro: a primeira legenda não pode ser adiantada para antes do segundo zero.");
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

}
