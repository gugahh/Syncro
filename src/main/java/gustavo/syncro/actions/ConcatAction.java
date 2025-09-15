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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Essa action faz o oposto da Split Action:
 * Pega os vários pedacoes de um arquivo que foi explodido em partes,
 * e os re-combina num arquivo unico.
 */
public class ConcatAction extends AbstractAction {

    /* implementacao do singleton */
    private static final ConcatAction instance = new ConcatAction();

    private ConcatAction(){}

    public static ConcatAction getInstance() {
        return instance;
    }

    private static final Pattern FILENAME_PATTERN = Pattern.compile(".{1,}_\\d{3}\\.srt");

    @Override
    public void doAction(String[] args) {

        // Parametros: [nomeDeArquivo] - deve corresponder ao pedaco 1.

        System.out.println("\nSyncro App - executando ConcatAction\n");

        SubtitleUtil sbtUtil = SubtitleUtil.getInstance();

        // Verificar se o nome do arquivo 1 foi corretamente informado.
        if ((args.length < 2) || (args[1] == null)) {
            System.out.println("Um nome de arquivo deve ser informado.");
            System.exit(-1);
        }

        // O nome de arquivo foi informado
        String nomeArq1 = args[1];

        // O nome do arquivo deve estar na mascara (nomearq_001.srt)
        if (!FILENAME_PATTERN.matcher(nomeArq1).matches()) {
            System.out.printf("\t- Erro: O arquivo informado [%s] nao atende a convencao de nomes (nomearq_001.srt).\n", nomeArq1);
            System.exit(-1);
        }

        String prefixoNmArquivo = "";
        int idxArquivo1 = 0;

        try {
            // Nome do arquivo, sem o hifen, sem a parte numerica, e sem a extensao.
            prefixoNmArquivo = nomeArq1.substring(0, (nomeArq1.length() - 8));

            //Extraindo a parte numerica do nome
            String parteNumericaStr = nomeArq1.substring((nomeArq1.length() - 7), (nomeArq1.length() - 4));
            idxArquivo1 = Integer.parseInt(parteNumericaStr);
        } catch (StringIndexOutOfBoundsException | NumberFormatException ex) {
            System.out.print("\t- Erro no tratamento do nome de arquivo. Codigo: -20003.\n");
            System.exit(-1);
        }

        //Verificando se o arquivo 1 existe, mesmo.
        File umArquivo = new File(nomeArq1);
        if (!umArquivo.exists()) {
            System.out.printf("\t- Erro: O arquivo %s nao foi encontrado na pasta atual.\n", nomeArq1);
            System.exit(-1);
        }

        System.out.printf("\t- Encontrado o arquivo [%s].\n", nomeArq1);

        List<PedacoLegenda> pedacsList = new ArrayList<>(); // Cada um dos arquivos e suas legendas
        PedacoLegenda umPedaco;         // Contera apenas o arquivo atual sendo processado
        List<Subtitle> listaLegendasTemp;

        // Tudo validado.
        // Agora faremos um loop para pegar cada um dos arquivos, sequencialmente,
        // ate nao existirem mais arquivos. O ultimo arquivo possivel eh o de indice 999.
        for (int idx = idxArquivo1; idx <= 999; idx++) {
            String nomeArquivo = String.format("%s_%03d.srt", prefixoNmArquivo, idx);
            System.out.printf("\tProcessando: " + nomeArquivo);

            File arqu2 = new File(nomeArquivo);
            if (arqu2.exists()) {
                System.out.print("\t- Arquivo encontrado!");
                try {
                    listaLegendasTemp = sbtUtil.obtemListaLegendasFromFile(nomeArquivo);
                    System.out.printf("\t- (%d legendas)\n", listaLegendasTemp.size());
                    umPedaco = new PedacoLegenda(idx, nomeArquivo, listaLegendasTemp);
                    pedacsList.add(umPedaco);
                } catch (ValidacaoException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.print("\t- Nao encontrado (nao ha mais partes a processar).\n");
                break;
            }
        }

        //Segundo loop: combina os pedacos em um unico array
        List<Subtitle> listaLegendasFinal = new ArrayList<>();
        for (PedacoLegenda p : pedacsList) {
            listaLegendasFinal.addAll(p.listaLegendas);
        }

        // Defininindo o nome do arquivo de Saida:  nome original + concat (arbitrei isso).
        String nomeArqFinal = prefixoNmArquivo + "_concat.srt";

        //Criando um backup (apenas se for sobrescrever o arquivo de saida.
        if ((new File(nomeArqFinal)).exists()) {
            System.out.printf("\n\t- Ja existe um arquivo com o nome %s. Vamos criar um backup do arquivo.\n", nomeArqFinal);

            BackupFileUtil bfu = new BackupFileUtil();
            try {
                String nomeArquivoBackup = bfu.makeBackupFromFile(nomeArqFinal);
                System.out.println("\t\tNome do arquivo (de backup) gerado: " + nomeArquivoBackup);
            } catch (FileBackupException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }

        //Salva as alterações no arquivo de saida.
        try {
            SubtitleFileUtil.saveChangedSubtitleFile(nomeArqFinal, listaLegendasFinal);
        } catch (ArquivoLegendaWriteException e) {
            System.out.println(e.getMessage());
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        System.out.println("\nExecutado com SUCESSO.\n");
    }

    /**
     * Contem um pedaco da legenda total sendo processado.
     */
    class PedacoLegenda {

        int indice;
        String nomeArquivo;
        List<Subtitle> listaLegendas;

        PedacoLegenda(int idx, String nomeArquivo, List<Subtitle> listaLegendas) {
            this.indice = idx;
            this.nomeArquivo = nomeArquivo;
            this.listaLegendas = listaLegendas;
        }
    }
}
