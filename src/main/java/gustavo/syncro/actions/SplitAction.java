package gustavo.syncro.actions;

import gustavo.syncro.Subtitle;
import gustavo.syncro.exceptions.ArquivoLegendaWriteException;
import gustavo.syncro.exceptions.validacao.ValidacaoException;
import gustavo.syncro.utils.HelpUtil;
import gustavo.syncro.utils.SubtitleFileUtil;
import gustavo.syncro.utils.SubtitleUtil;

import java.util.List;

/**
 * Permite quebrar uma legendas em N outras, de modo a facilitar
 * a traducao dos pedacoes utilizando uma IA gratuita.
 */
public class SplitAction extends AbstractAction {

    /* implementacao do singleton */
    private static final SplitAction instance = new SplitAction();

    private boolean fazerBackupLegenda;

    private SplitAction(){}

    public static SplitAction getInstance() {
        return instance;
    }

    @Override
    public void doAction(String[] args) {

        // Parametros: [nomeLegenda] [qt_legendas_por_arquivo]

        boolean fazerBackupLegenda = true;

        SubtitleUtil sbtUtil = SubtitleUtil.getInstance();

        List<Subtitle> listaLegendas;

        if(args.length != 3){   //Todos os parametros sao obrigatorios.
            System.out.println("\tNumero de parametros incorreto");
            System.out.println("\tpara realizar esta operacao.");
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        System.out.println("Syncro App - Modo Split selecionado.");
        System.out.println("\nArquivo a ser processado: " + args[1]);

        // args[1] eh o arquivo de legenda.

        int quantLegPorArquivo = 0;
        try{
            quantLegPorArquivo = Integer.parseInt(args[2]);
        } catch (NumberFormatException nfe) {
            //Parâmetro inválido. Unica opção é -nobak.
            System.out.println("Erro: O valor informado para a quantidade de legendas por arquivo nao eh numerico.\n");
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        System.out.println("\tQuantidade de legendas por arquivo desejada: " + quantLegPorArquivo);

        // Quant de legendas por arquvio deve ser entre 1 e 200).
        if (quantLegPorArquivo < 1 || quantLegPorArquivo > 200) {
            System.out.println("Erro: A quantidade de legendas por arquivo deve ser entre 1 e 200.\n");
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        // Carregando as legendas a partir do arquivo.
        try {
            listaLegendas = sbtUtil.obtemListaLegendasFromFile(args[1]);
        } catch (ValidacaoException e) {
            throw new RuntimeException(e);
        }

        if (null == listaLegendas || listaLegendas.isEmpty()) {
            System.out.println("Erro: A lista de Legendas eh nula ou vazia.");
            System.out.println(HelpUtil.howToGetHelpStr);
            System.exit(-1);
        }

        System.out.println("\tQuantidade de legendas no arquivo de origem: " + listaLegendas.size());

        //Precisamos do "nome" do arquivo (sem a extensao .srt) para nosso(s) arquivo(s) de saida.
        String prefixoArquivo = args[1].substring(0, args[1].length()-4);

        // Se chegou aqui, tudo certo. Podemos criar os nossos arquivos!
        int iteracao = 1;
        int posInicial = 0;
        int posFinal;
        List<Subtitle> subLista;
        String nomeDoArquivoParte;

        while (posInicial < listaLegendas.size()) {

            // 99 ou a ultima posicao do arquivo de legendas, o que for menor.
            // Nao podemos ultrapassar o tamanho do arquivo!
            posFinal = Math.min( (quantLegPorArquivo * iteracao - 1), (listaLegendas.size() - 1));

            //Ex: legendas 1 a 100 = listaLegendas[0] a listaLegendas[99].
            subLista = listaLegendas.subList(posInicial, posFinal + 1);

            //Gravando pro Filesystem o arquivo com este "pedaco":
            nomeDoArquivoParte = prefixoArquivo + "_" + String.format("%03d", iteracao) + ".srt";
            try {
                SubtitleFileUtil.saveChangedSubtitleFile(nomeDoArquivoParte, subLista);
            } catch (ArquivoLegendaWriteException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }

            // Se preparando para pegar o proximo "pedaco".
            posInicial = posFinal + 1;
            iteracao++;
        }

        System.out.println("\nArquivos de legenda particionados gerados com SUCESSO.");
    }
}
