package gustavo.syncro.actions;

import gustavo.syncro.Subtitle;
import gustavo.syncro.exceptions.ArquivoLegendaWriteException;
import gustavo.syncro.exceptions.PosicaoLegendaInvalidaException;
import gustavo.syncro.exceptions.validacao.ValidacaoException;
import gustavo.syncro.exceptions.validacao.timestamp.TimestampInvalidoException;
import gustavo.syncro.exceptions.validacao.timestamp.TimestampNuloException;
import gustavo.syncro.utils.HelpUtil;
import gustavo.syncro.utils.SubtitleFileUtil;
import gustavo.syncro.utils.SubtitleUtil;
import gustavo.syncro.utils.timeconverter.AbstractTimeConverter;
import gustavo.syncro.utils.timeconverter.DataCompletaConverter;

import java.util.List;

public class TimeAdjustAction extends AbstractAction {

    // Implementacao do Singleton.
    private static final TimeAdjustAction instance = new TimeAdjustAction();

    private TimeAdjustAction(){}

    // private constructor to avoid client applications using the constructor
    public static TimeAdjustAction getInstance() {
        return instance;
    }

    //TODO: Resolver o backup de legenda.
    private boolean fazerBackupLegenda;

    /**
     * Uma vez entendido que a acao a ser realizada eh o ajuste de tempo de legendas,
     * todo o trabalho eh passado para esse classe; Validacoes e transformacoes
     * devem estar aqui, sendo que validacoes devem retornar excessoes.
     * args sao os argumentos da linha de comando (Main)
     *
     * @param args argumentos da linha de comando.
     */
    @Override
    public void doAction(String[] args) {

        SubtitleUtil sbtUtil = SubtitleUtil.getInstance();

        // Vai conter a lista das legendas, ja tratadas.
        List<Subtitle> listaLegendas = null;

        if(args.length <= 2){ //Usu informou arquivo, mas nao o tempo de ajuste
            System.out.println("\tO parametro tempo (de ajuste) e obrigatorio");
            System.out.println("\tpara realizar esta operacao.");
            System.out.println("\tVerifique os parametros de entrada.");
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(0);
        }

        /* Nº de params e legal. Pode-se tentar comecar.
         * No caso de qualquer método falhar a operação
         * (execução da App) deve ser abortada. */

        // args[0] args[1]   args[2]  args[3](opc)     args[3 ou 4] (opc)
        //[-adjust [arquivo] [tempo] [indiceLegenda] ] [-nobak]

        String indiceLegendaInicial = null; //default caso usuário não passe uma referência de índice

        if(args.length >= 4){
            //Args[3] pode ser um índice de legenda (opc) ou -nobak.
            if(args[3].equalsIgnoreCase("-nobak") ||
                    args[3].equalsIgnoreCase("-noback")){ //Usu solicitou não fazer backup
                fazerBackupLegenda = false;
                System.out.println("Fazer Backup - falta implementar");
            } else indiceLegendaInicial = args[3];
        }

        if(args.length == 5){ //O quarto parametro SÓ PODE ser referente ao Backup.
            if(args[4].equalsIgnoreCase("-nobak")){ //Usu solicitou não fazer backup
                fazerBackupLegenda = false;
                System.out.println("Fazer Backup - falta implementar");
            } else { //Parâmetro inválido (deveria ser -nobak, ou não existir).
                System.out.println("\tParametro invalido / desconhecido.");
                System.out.println(HelpUtil.howToGetHelpStr);
                System.exit(-1);
            }
        }

        /* Nº de parâmetros passados e ordem é correta.
         * Testando a consistência de cada um dos parâmetros. */
        String msgTesteAjuste = this.testaParamsEntradaAdjust(args[1], args[2], indiceLegendaInicial); //Caso passe, tudo Ok.
        if (msgTesteAjuste != null) {
            System.out.println(msgTesteAjuste);
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        try {
            listaLegendas = sbtUtil.obtemListaLegendasFromFile(args[1]);
        } catch (ValidacaoException e) {
            throw new RuntimeException(e);
        }

        if (null == listaLegendas || listaLegendas.isEmpty()) {
            throw new RuntimeException("Lista de Legendas eh nula ou vazia.");
        }

        //Efetua alterações de tempo solicitadas.
        int indicelegendaInt = 1;

        //caso o Usu tenha informado um valor válido de em qual legenda iniciar, utilizá-lo.
        if(indiceLegendaInicial!= null) {
            indicelegendaInt = Integer.parseInt(indiceLegendaInicial);
        }

        int tempoEmMillis = 0;
        try {
            AbstractTimeConverter timeConverter = getTimeConverter(args[2]);
            tempoEmMillis = timeConverter.getMillisFromString(args[2]);
        } catch (TimestampNuloException | TimestampInvalidoException e) {
            System.out.println(e.getMessage());
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        // Efetivamente ajustando o tempo.
        try {
            this.modificaTempoTodasLegendas(listaLegendas, tempoEmMillis,indicelegendaInt);
        } catch (PosicaoLegendaInvalidaException plie) {
            System.out.println(plie.getMessage());
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        //TODO: Adicionar o Backup (se tudo deu certo, agora eh a hora de faze-lo).

        //Salva as alterações no arquivo de origem.
        // s.saveChangedSubtitleFile(args[1]);
        try {
            SubtitleFileUtil.saveChangedSubtitleFile(args[1], listaLegendas);
        } catch (ArquivoLegendaWriteException e) {
            System.out.println(e.getMessage());
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        System.out.println("O Tempo das Legendas foi ajustado com sucesso.");
    }

    /**
     * Utilizado para testar a consistência dos parâmetros enviados,
     * Quando a opção selecionada for AJUSTAR o tempo das legendas.
     * Qualquer inconsistência fará o aplicativo dar exit().
     * @param fileName arquivo SRT a ser testado
     * @param tempoAjuste tempo de ajuste
     * @param indiceLegendaInicial indice da legenda inicial
     * @return o erro encontrado, ou null no caso de nao haver erros.
     */
    public String testaParamsEntradaAdjust(String fileName,
                                           String tempoAjuste,
                                           String indiceLegendaInicial) {

        // Testando a consistência do arquivo de legenda enviado.
        if(!SubtitleFileUtil.fileExists(fileName)){
            return "\tErro: O arquivo solicitado nao existe.";
        }

        /* Testando o tempo de ajuste informado;
        *  obter um converter adequado significa que provavelmente a conversao
        *  vai funcionar. */
        try {
            AbstractTimeConverter timeConverter = getTimeConverter(tempoAjuste);
        } catch (TimestampNuloException | TimestampInvalidoException e) {
            return (e.getMessage());
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

    /**
     * Obtem o conversor de tempo adequado, de acordo com o texto (com um timestamp)
     * fornecido.
     *
     * @param umTimeStamp texto contendo um timestamp (ex: +00:00:02s)
     * @return instancia de AbstractTimeConverter
     */
    AbstractTimeConverter getTimeConverter(String umTimeStamp)
            throws TimestampNuloException, TimestampInvalidoException {

        final AbstractTimeConverter[] conversoresList = {
                //TODO: aqui outros conversores serao espetados,
                // para dar suporte a formatacoes de datas diferentes;
                // Por hora, so temos este conversor aqui.
                new DataCompletaConverter()
        };

        for(AbstractTimeConverter timeConverter: conversoresList) {
            if(timeConverter.isAcceptedFormat(umTimeStamp)) {
                return timeConverter; // Obs: qualquer um dos conversores pode lancar TimestampNuloException.
            }
        }

        // Se chegou aqui e nao achou um conversor valido... temos um problema.
        throw new TimestampInvalidoException("O formato de data informado está errado, ou não é suportado");
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
