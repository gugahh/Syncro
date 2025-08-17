package gustavo.syncro.utils;

import gustavo.syncro.exceptions.FileReadException;

import java.io.*;
import java.util.List;

public class FileReaderUtil {

    public static final String ERRO_LEITURA_LEGENDA = "Ocorreu um erro ao ler o arquivo de legenda %s";

    /**
     * Le um arquivo fornecido, e o atribui cada uma das linhas do arquivo
     * ao List<String> definido em stringList.
     * Caso fazerBackupLegenda seja true, faz concorrentemente uma copia (backup) do arquivo.
     * @param fileName
     * @param stringList
     * @param fazerBackupLegenda
     * @throws FileReadException
     * @throws IOException
     */
    public static void readFromFile(String fileName,
                      List<String> stringList,
                      boolean fazerBackupLegenda
                      ) throws FileReadException, IOException {

        File arquivoBackup;
        File arquivoLegenda = new File(fileName);

        LineNumberReader bfread = null;
        PrintWriter writer = null;
        String currentLine;

        try {
            bfread = new LineNumberReader(new FileReader(arquivoLegenda)); //Tentado abrir arquivo. Operação pode falhar.

            if (fazerBackupLegenda) {
                arquivoBackup = new File("Backup_" + fileName.replace(".srt", "") + "_" + System.currentTimeMillis() + ".srt");
                writer = new PrintWriter(new FileWriter(arquivoBackup));
            }

            while ((currentLine = bfread.readLine()) != null) { // Loop: Lendo todas as linhas do arquivo texto até o fim.

                //Escrevendo cópia backup
                if (fazerBackupLegenda) {
                    writer.println(currentLine);
                }

                //Carregando ArrayList
                currentLine = currentLine.trim();
                if (currentLine.length() > 0) { //Evitando linhas em branco
                    stringList.add(currentLine);
                }
            }
        } catch(NullPointerException np) {
            throw new FileReadException(String.format(ERRO_LEITURA_LEGENDA, stringList), np);
        } catch (IOException ioex) {
            throw new FileReadException(String.format(ERRO_LEITURA_LEGENDA, stringList), ioex);

        } finally {
            if(bfread!=null){
                bfread.close();
            }
            if(writer!=null){
                writer.close();
            }
        }
    }
}
