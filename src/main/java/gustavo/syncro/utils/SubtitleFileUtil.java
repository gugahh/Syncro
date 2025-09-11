package gustavo.syncro.utils;

import gustavo.syncro.Subtitle;
import gustavo.syncro.exceptions.ArquivoLegendaWriteException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class SubtitleFileUtil {

    public static final String ERRO_ESCRITA_LEGENDAS = "Erro ao escrever o arquivo de legendas %s";

    /**
     * Salva um arquivo de legendas, a partir da lista
     * de legendas ja modificada.
     * Sobrescreve o arquivo existente, se existir.
     *
     * @param fileName nome do arquivo
     * @param legendasList lista de legendas para persistir.
     * @throws ArquivoLegendaWriteException caso ocorre qualquer erro.
     */
    public static void saveChangedSubtitleFile(
            String fileName, List<Subtitle> legendasList) throws ArquivoLegendaWriteException {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileWriter(fileName));

            for(Subtitle st : legendasList){
                writer.println(st.getId());
                writer.print(st.getStartTimeAsString());
                writer.print(" --> ");
                writer.println(st.getEndTimeAsString());
                writer.println(st.getTexto());
                writer.println(); //Legendas são separadas por uma linha em branco.
            }
            writer.println(); //A legenda é fechada com uma linha em branco a mais.

        } catch (IOException e) {
            throw new ArquivoLegendaWriteException(
                    String.format(ERRO_ESCRITA_LEGENDAS, fileName), e);
        } finally {
            if(writer!=null){
                writer.close();
            }
        }
    }

    /**
     * Verifica se um arquivo existe no sistema de arquivos.
     * @param filename nome do arquivo a ser testado
     * @return boolean
     */
    public static boolean fileExists(String filename) {
        File umArquivo = new File(filename);
        return umArquivo.exists();
    }
}
