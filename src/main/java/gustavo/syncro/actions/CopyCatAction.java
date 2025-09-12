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
 * Executa o metodo CopyCat: copia os timestamps das legendas do arquivo
 * de origem para o arquivo de destino, sem alterar o texto das legendas, em si.
 * Eh util para acertar os tempos de duas legendas do mesmo filme, de
 * idiomas diferentes, porem em que uma esta com o tempo certo e a outra, nao.
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

        System.out.println("Entrou em CopyCatAction");

        boolean fazerBackupLegenda = true;

        SubtitleUtil sbtUtil = SubtitleUtil.getInstance();

        if(args.length < 6 || args.length > 7){ //Num de params diferente do esperado.
            System.out.println("\tERRO: Numero de parametros incorreto");
            System.out.println("\tpara realizar esta operacao.");
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        if (args.length == 7) {
            //Args[6] só pode ser -nobak.
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

        System.out.println("nomeArquivoOrigem: " + nomeArquivoOrigem);
        System.out.println("nomeArquivoDest: " + nomeArquivoDest);

        int idxInicArqOrig;
        int idxFimArqOrig;
        int idxInicArqDest;

        try {
            idxInicArqOrig = Integer.parseInt(args[2]);
            idxFimArqOrig = Integer.parseInt(args[3]);
            idxInicArqDest = Integer.parseInt(args[5]);

            System.out.println("idxInicArqOrig: " + idxInicArqOrig);
            System.out.println("idxFimArqOrig:" + idxFimArqOrig);
            System.out.println("idxInicArqDest: " + idxInicArqDest);
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
            System.out.println("Lista de Legendas de Origem eh nula ou vazia.");
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }
        System.out.println("Tamanho da Lista de Legendas de Origem: " + listaLegendasOrigem.size());

        // Carregando a Lista de Legendas do arquivo de Destino
        List<Subtitle> listaLegendasDest;
        try {
            listaLegendasDest = sbtUtil.obtemListaLegendasFromFile(nomeArquivoDest);
        } catch (ValidacaoException e) {
            throw new RuntimeException(e);
        }

        if (null == listaLegendasDest || listaLegendasDest.isEmpty()) {
            // throw new RuntimeException("Lista de Legendas eh nula ou vazia.");
            System.out.println("Lista de Legendas de Destino eh nula ou vazia.");
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }
        System.out.println("Tamanho da Lista de Legendas de Destino: " + listaLegendasDest.size());

        //Todo: verificar se o indice inicial e final do arquivo de origem sao validos.

        //Todo: verificar se o indice inicial do arquivo de destino e valido.

        //Todo: caso o indice do arquivo de destino seja diferente de 1,
        // Caso o ajuste seja negativo (adiantar), verificar se o timestamp AJUSTADO
        // no arquivo de destino "atropela" a legenda anterior.

        //Todo: veficar, apos o processamento, se o arquivo de origem, ja considerado
        // os indices, eh MAIOR (mais legendas) que o arquivo de destino. Exibir Warning.

        // Todo: Fazer a copia dos timestamps, ignorando os indices.
        int idxOrigem = 0;
        int idxDestino = 0;
        int posDeParada = 3; // Indice da ultima legenda a processar, ajustado usando indice inic = 0.

        while (idxOrigem <= posDeParada) {
            Subtitle sbOrigem = listaLegendasOrigem.get(idxOrigem);
            Subtitle sbDestino = listaLegendasDest.get(idxDestino);

            sbDestino.setStartTime(sbOrigem.getStartTime());
            sbDestino.setEndTime(sbOrigem.getEndTime());

            idxOrigem++;
            idxDestino++;
        }

        // Todo: alterar o codigo acima, considerando os indices.

        //Criar o Backup, se desejado (se tudo deu certo, agora eh a hora de faze-lo).
        if(fazerBackupLegenda) {
            BackupFileUtil bfu = new BackupFileUtil();
            try {
                System.out.println("Gerando um backup do arquivo de destino original...");
                String nomeNovoArquivo = bfu.makeBackupFromFile(nomeArquivoDest);
                System.out.println("\tNome do arquivo (backup) gerado: " + nomeNovoArquivo);
            } catch (FileBackupException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }

        //Salva as alterações no arquivo de origem.
        try {
            SubtitleFileUtil.saveChangedSubtitleFile(nomeArquivoDest, listaLegendasDest);
        } catch (ArquivoLegendaWriteException e) {
            System.out.println(e.getMessage());
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        System.out.println("Executou com SUCESSO.");
    }

    /**
     * Efetua a validacao dos parametros de entrada
     * (quanto ao preenchimento e conformidade).
     * No caso de sucesso, retorna nulo
     *
     * @param args argumentos da linha de comando
     * @return nulo, ou um erro.
     */
    private String testaParamsEntrada(String[] args) {
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
