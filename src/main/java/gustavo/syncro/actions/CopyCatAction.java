package gustavo.syncro.actions;

import gustavo.syncro.Subtitle;
import gustavo.syncro.exceptions.ArquivoLegendaWriteException;
import gustavo.syncro.exceptions.validacao.FileBackupException;
import gustavo.syncro.exceptions.validacao.ValidacaoException;
import gustavo.syncro.utils.BackupFileUtil;
import gustavo.syncro.utils.HelpUtil;
import gustavo.syncro.utils.SubtitleFileUtil;
import gustavo.syncro.utils.SubtitleUtil;

import java.io.File;
import java.util.List;

/**
 * CopyCat: copia os timestamps das legendas do arquivo
 * de origem para o arquivo de destino, sem alterar o texto das legendas, em si.
 * Eh util para acertar os tempos de duas legendas do mesmo filme, de
 * idiomas diferentes, porem em que uma esta com o tempo certo e a outra, nao.
 * Criado em set/2025.
 */
public class CopyCatAction extends AbstractAction {

    /* implementacao do singleton */
    private static final CopyCatAction instance = new CopyCatAction();

    private CopyCatAction(){}

    public static CopyCatAction getInstance() {
        return instance;
    }

    @Override
    public void doAction(String[] args) {
        // Parametros: [nomeArquOrigem] [idxInicioOrig] [idxFinalOrig] [nomeArqDest] [idxInicioDest] [-nobak]
        //              [1]             [2]             [3]            [4]           [5]             [6]

        System.out.println("\nSyncro App - executando CopyCatAction\n");

        SubtitleUtil sbtUtil = SubtitleUtil.getInstance();

        if(args.length < 6 || args.length > 7){ //Num de params diferente do esperado.
            System.out.println("\tERRO: Numero de parametros incorreto");
            System.out.println("\tpara realizar esta operacao.");
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        boolean fazerBackupLegenda = true;

        if (args.length == 7) {
            //Como existe um Args[6], este só pode ser -nobak.
            if (args[6].equalsIgnoreCase("-nobak") || args[6].equalsIgnoreCase("-noback")) { //Usu solicitou não fazer backup
                fazerBackupLegenda = false;
            } else {
                //Parâmetro inválido. Unica opção é -nobak.
                System.out.println("\tparametro incorreto");
                System.out.println(HelpUtil.howToGetHelpStr);
                System.exit(-1);
            }
        }

        /* Nº de parâmetros passados e ordem é correta.
         * Testando a consistência de cada um dos parâmetros. */
        String retornoValidacao = this.testaParamsEntrada(args);
        if (retornoValidacao != null) {
            System.out.println(retornoValidacao);
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        //Parametros de entrada. Criando variaveis mais amigaveis, ja do tipo certo.
        String nomeArquivoOrigem = args[1];
        String nomeArquivoDest = args[4];

        System.out.println("\t- Arquivo de Origem: " + nomeArquivoOrigem);
        System.out.printf("\t\t- Indices de legenda a serem copiados: de [%s] a [%s].%n", args[2], args[3]);
        System.out.println("\n\t- Arquivo de Destino: " + nomeArquivoDest);
        System.out.printf("\t\t- Indices de legenda a serem sobrescritos: de [%s] em diante.\n%n", args[5]);

        int idxInicArqOrig = 0;
        int idxFimArqOrig = 0;
        int idxInicArqDest = 0;

        try {
            idxInicArqOrig = Integer.parseInt(args[2]);
            idxFimArqOrig = Integer.parseInt(args[3]);
            idxInicArqDest = Integer.parseInt(args[5]);

//            System.out.println("idxInicArqOrig: " + idxInicArqOrig);
//            System.out.println("idxFimArqOrig:" + idxFimArqOrig);
//            System.out.println("idxInicArqDest: " + idxInicArqDest);
        } catch(NumberFormatException e) {
            System.out.println("ERRO inesperado. Codigo -20001"); //Erro de programacao!
            System.exit(-1);
        }

        // Carregando a Lista de Legendas do arquivo de Origem
        List<Subtitle> listaLegendasOrigem;
        try {
            listaLegendasOrigem = sbtUtil.obtemListaLegendasFromFile(nomeArquivoOrigem);
        } catch (ValidacaoException e) {
            throw new RuntimeException(e);
        }

        if (null == listaLegendasOrigem || listaLegendasOrigem.isEmpty()) {
            // throw new RuntimeException("Lista de Legendas eh nula ou vazia.");
            System.out.println("Erro: Lista de Legendas de Origem eh nula ou vazia.");
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        // Carregando a Lista de Legendas do arquivo de Destino
        List<Subtitle> listaLegendasDest;
        try {
            listaLegendasDest = sbtUtil.obtemListaLegendasFromFile(nomeArquivoDest);
        } catch (ValidacaoException e) {
            throw new RuntimeException(e);
        }

        if (null == listaLegendasDest || listaLegendasDest.isEmpty()) {
            System.out.println("Erro: Lista de Legendas de Destino eh nula ou vazia.");
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }
        System.out.printf("\t- Tamanho das Lista de Legendas - Origem: [%d] - Destino: [%d]%n",
                listaLegendasOrigem.size(), listaLegendasDest.size());

        // Realizando validacoes sobre os arquivos fornecidos.
        validaArquivos(idxInicArqOrig, idxFimArqOrig, idxInicArqDest, listaLegendasOrigem, listaLegendasDest);

        // Fazendo a copia dos timestamps, ja considerando os indices desejados
        int idxOrigem = idxInicArqOrig - 1;
        int idxDestino = idxInicArqDest - 1;

        // Indice da ultima legenda a processar, ajustado usando indice inic = 0.
        int posDeParada = (idxFimArqOrig > 0) ? (idxFimArqOrig - 1) : (listaLegendasOrigem.size() - 1);
        // System.out.println("posDeParada: " + posDeParada);

        // OBS: A lista de origem pode ser maior que a de destino, isso eh permitido.
        while (idxOrigem <= posDeParada &&
                idxDestino < listaLegendasDest.size()) { // Evitando ultrapassar o fim do arquivo de destino.

            Subtitle sbOrigem = listaLegendasOrigem.get(idxOrigem);
            Subtitle sbDestino = listaLegendasDest.get(idxDestino);

            sbDestino.setStartTime(sbOrigem.getStartTime());
            sbDestino.setEndTime(sbOrigem.getEndTime());

            /*
            System.out.println("\t\tAtualizou a legenda de destino: " + idxDestino);
            System.out.println("\t\t\tUtilizando o novo timestamp: " +
                    sbOrigem.getStartTimeAsString() + " - " + sbOrigem.getEndTimeAsString());
             */

            idxOrigem++;
            idxDestino++;
        }

        //Criar o Backup, se desejado (se tudo deu certo, agora eh a hora de faze-lo).
        if(fazerBackupLegenda) {
            BackupFileUtil bfu = new BackupFileUtil();
            try {
                System.out.println("\n\t- Gerando um backup do arquivo de destino original...");
                String nomeNovoArquivo = bfu.makeBackupFromFile(nomeArquivoDest);
                System.out.println("\t\tNome do arquivo (backup) gerado: " + nomeNovoArquivo);
            } catch (FileBackupException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }

        //Salva as alterações no arquivo de Destino.
        try {
            SubtitleFileUtil.saveChangedSubtitleFile(nomeArquivoDest, listaLegendasDest);
        } catch (ArquivoLegendaWriteException e) {
            System.out.println(e.getMessage());
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        System.out.println("\nOs timestamps das legendas do arquivo de Destino foram ajustados com SUCESSO.");
    }

    /**
     * Efetua as validacoes das Liatas de Legendas de Origem e Destino,
     * considerando os parametros de processamento fornecidos.
     * Realiza System.exit(-1) em caso de erro, e exibe observacoes quando pertinente.
     *
     * @param idxInicArqOrig - indice inicial do arquivo de origem
     * @param idxFimArqOrig - indice final do arquivo de origem
     * @param idxInicArqDest  - indice final do arquivo de origem
     * @param listaLegendasOrigem - Lista de Legendas de Origem
     * @param listaLegendasDest - Lista de Legendas de Destino
     */
    void validaArquivos(int idxInicArqOrig,
                        int idxFimArqOrig,
                        int idxInicArqDest,
                        List<Subtitle> listaLegendasOrigem,
                        List<Subtitle> listaLegendasDest
                        ) {
        boolean existeErro = false;
        String msgErro = "";

        // Verificando, caso indice final do arquivo de origem diferente de (0, -1),
        // se o indice final eh maior que o inicial.
        if (idxFimArqOrig > 0 && idxFimArqOrig < idxInicArqOrig) {
            existeErro = true;
            msgErro = "O indice final da legenda de origem nao pode ser menor que o indice inicial.";
        }

        //Verificando se o indice inicial e final do arquivo de origem sao validos.
        // leia-se: aqueles indices existem mesmo no arquivo de origem.
        if (!existeErro && idxInicArqOrig > listaLegendasOrigem.size()) {
            existeErro = true;
            msgErro = "O indice inicial da legenda de origem nao eh valido. " +
                    "O arquivo da legenda possui apenas " + listaLegendasOrigem.size() + " legendas, " +
                    "mas foi informado o indice " + idxInicArqOrig;
        }
        if (!existeErro && idxFimArqOrig > 0 && idxFimArqOrig > listaLegendasOrigem.size()) {
            existeErro = true;
            msgErro = "O indice final da legenda de origem nao eh valido. " +
                    "O arquivo da legenda possui apenas " + listaLegendasOrigem.size() + " legendas, " +
                    "mas foi informado o indice " + idxFimArqOrig;
        }

        // --
        // Verificar se o indice inicial do arquivo de destino e valido.
        if (!existeErro && idxInicArqDest > listaLegendasDest.size()) {
            existeErro = true;
            msgErro = "O indice inicial da legenda de destino nao eh valido. " +
                    "O arquivo da legenda possui apenas " + listaLegendasDest.size() + " legendas, " +
                    "mas foi informado o indice " + idxInicArqDest;
        }

        // Caso o indice do arquivo de destino seja diferente de 1,
        // verificar se o timestamp AJUSTADO no arquivo de destino "atropela" a legenda anterior.
        if (!existeErro && idxInicArqDest > 1) {
            Subtitle legDestAtual = listaLegendasDest.get(idxInicArqDest - 1);
            Subtitle legDestAnterior = listaLegendasDest.get(idxInicArqDest - 2);
            int timestampFinalLegAnt = legDestAnterior.getEndTime();

            Subtitle legOrigemInic = listaLegendasOrigem.get(idxInicArqOrig - 1);
            int timestampInicLegOrig = legOrigemInic.getStartTime();

            if (timestampInicLegOrig < timestampFinalLegAnt) {
                existeErro = true;
                msgErro = "O ajuste solicitado faria com que a legenda do arquivo de destino " +
                        "imediatamente anterior a que foi solicitada (que seria a legenda " +
                        (idxInicArqDest  - 1) + ") ficasse com um timestamp POSTERIOR a lengenda " +
                        "sendo ajustada (" + idxInicArqDest + "), o que nao e permitido." +
                        "\n Ts Final da legenda Anterior (" + (idxInicArqDest  - 1) + ") (no arq de destino) (" +
                        (idxInicArqDest - 1) + "): " + legDestAnterior.getEndTimeAsString() +
                        "\n Ts de Inicio da Legenda Atual (no arq de destino) (" + idxInicArqDest + ") especificada (" +
                        idxInicArqDest + "): " + legDestAtual.getStartTimeAsString() +
                        "\n Timestamp de inicio desejado (invalido) do arq de origem: " +
                        legOrigemInic.getStartTimeAsString() + ".";
            }
        }

        // Se ocorreu qualquer erro de validacao nos testes acima... sair com erro.
        if (existeErro) {
            System.out.println("Erro: " + msgErro);
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        int qtLegendasOrigem = ((idxFimArqOrig > 0) ? idxFimArqOrig : listaLegendasOrigem.size()) - idxInicArqOrig;
        int qtLegendasDest = listaLegendasDest.size() - idxInicArqDest;

        // System.out.println("qtLegendasOrigem: " + qtLegendasOrigem);
        // System.out.println("qtLegendasDest: " + qtLegendasDest);

        if ((qtLegendasOrigem - qtLegendasDest) == 0) {
            System.out.println("\n\t- O arquivo de origem tem o mesmo numero de legendas,\n"+
                    "\t  no intervalo especificado, que o arquivo de destino;\n" +
                    "\t  todas as condicoes foram satisfeitas. Iniciando a sincronizacao.");
        }

        // Verificando se o arquivo de origem, ja considerando os indices desejados,
        // eh MAIOR (mais legendas) que o arquivo de destino. Exibir Warning (mas executar).
        if ((qtLegendasOrigem - qtLegendasDest) > 0) {
            System.out.println("\n\t- Atencao: O arquivo de origem tem MAIS legendas,\n"+
                    "\t  no intervalo especificado, que o arquivo de destino;\n" +
                    "\t  isso eh permitido, mas esteja informado que existe essa diferenca de tamanho.");
        }

        if ((qtLegendasOrigem - qtLegendasDest) < 0) {
            System.out.println("\n\t- Atencao: O arquivo de origem tem MENOS legendas,\n" +
                    "\t  no intervalo especificado, que o arquivo de destino;\n" +
                    "\t  isso NAO eh permitido, uma vez que ficariam legendas, no final do arquivo\n" +
                    "\t  de destino, sem ajustar os tempos.\n" +
                    "\t  Considere ajustar os intervalos de inicio e fim do arquivo de origem.");
            System.exit(-1);
        }
    }

    /**
     * Efetua a validacao dos parametros de entrada
     * (quanto ao preenchimento e conformidade).
     * No caso de sucesso, retorna nulo
     *
     * @param args argumentos da linha de comando
     * @return nulo, ou um erro.
     */
    String testaParamsEntrada(String[] args) {
        // Parametros: [nomeArquOrigem] [idxInicioOrig] [idxFinalOrig] [nomeArqDest] [idxInicioDest] [-nobak]
        //              [1]             [2]             [3]            [4]           [5]             [6]

        // Testando a existencia do arquivo de legenda de ORIGEM enviado.
        File arquivoOrigem = new File(args[1]);
        if(!arquivoOrigem.exists()){
            return "\tErro: O arquivo de Origem nao existe.";
        }

        // Testando a existencia do arquivo de legenda de DESTINO enviado.
        File arquivoDestino = new File(args[4]);
        if(!arquivoDestino.exists()){
            return "\tErro: O arquivo de Destino nao existe.";
        }

        // Indice do Inicio do Arquivo de Origem (>=1)
        try {
            int idxInicArqOrig = Integer.parseInt(args[2]);
            if(idxInicArqOrig < 1){
                return "\tErro: O indice do inicio do arquivo de origem deve ser maior ou igual a 1.";
            }
        } catch(NumberFormatException e) {
            return "\tErro: O indice do inicio do arquivo de origem deve ser um numero inteiro.";
        }

        // Indice do Fim do Arquivo de Origem; Zero e -1 tambem sao valores validos.
        try {
            int idxFimArqOrig = Integer.parseInt(args[3]);
            if (idxFimArqOrig < -1){
                return "\tErro: o indice do fim do arquivo de origem deve ser maior ou igual a 1,"
                        + "\n\tOu entao 0 ou -1 para caso deseje copiar os indices das legendas ate o fim do arquivo.";
            }
        } catch(NumberFormatException e) {
            return "\tErro: O indice do fim do arquivo de origem deve ser um numero inteiro.";
        }

        // Indice do Inicio do Arquivo de Destino (>=1)
        try {
            int idxInicArqDest = Integer.parseInt(args[5]);
            if(idxInicArqDest < 1){
                return "\tErro: o indice do inicio do arquivo de destino deve ser maior ou igual a 1.";
            }
        } catch(NumberFormatException e) {
            return "\tErro: O indice do inicio do arquivo de destino deve ser um numero inteiro.";
        }

        return null;    //Sucesso.
    }
}
