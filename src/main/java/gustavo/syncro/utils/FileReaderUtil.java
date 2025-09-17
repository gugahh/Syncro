package gustavo.syncro.utils;

import gustavo.syncro.exceptions.FileReadException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileReaderUtil {

    public static final String ERRO_LEITURA_LEGENDA = "Ocorreu um erro ao ler o arquivo de legenda %s";

    /**
     * Le um arquivo fornecido, e a partir deste cria um List<String>,
     * contanto, em cada item da lista, uma das linhas do arquivo.
     * Remove automaticamente as linhas em branco.
     *
     * @param fileName nome do arquivo a ser processado.
     * @return lista de Strings
     * @throws FileReadException
     * @throws IOException
     */
    public List<String> readFromFile(String fileName
    ) throws FileReadException, IOException {
        return this.readFromFile(fileName, true);
    }

    /**
     * Le um arquivo fornecido, e a partir deste cria um List<String>,
     * contanto, em cada item da lista, uma das linhas do arquivo.
     * Nao Remove automaticamente as linhas em branco.
     *
     * @param fileName nome do arquivo a ser processado.
     * @param removeLinhasEmBranco caso true, remove as linhas em branco.
     * @return lista de Strings
     * @throws FileReadException
     * @throws IOException
     */
    public List<String> readFromFile(String fileName,
                                            boolean removeLinhasEmBranco
    ) throws FileReadException, IOException {
        List<String> stringList = new ArrayList<>();

        File arquivoLegenda = new File(fileName);

        LineNumberReader bfread = null;
        String currentLine;

        try {
            bfread = new LineNumberReader(new FileReader(arquivoLegenda)); //Tentado abrir arquivo. Operação pode falhar.

            while ((currentLine = bfread.readLine()) != null) { // Loop: Lendo todas as linhas do arquivo texto até o fim.

                //Carregando ArrayList
                currentLine = currentLine.trim();
                if (removeLinhasEmBranco && !currentLine.isEmpty()) { //Evitando linhas em branco, se solicitado.
                    stringList.add(currentLine);
                }
            }
        } catch(NullPointerException | IOException ex) {
            throw new FileReadException(String.format(ERRO_LEITURA_LEGENDA, stringList), ex);
        } finally {
            if(bfread!=null){
                bfread.close();
            }
        }

        return stringList;
    }

}
