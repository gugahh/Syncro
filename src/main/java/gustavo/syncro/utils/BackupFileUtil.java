package gustavo.syncro.utils;

import gustavo.syncro.exceptions.validacao.FileBackupException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupFileUtil {

    /**
     * Cria uma copia de um arquivo de legenda, adicionando o timestamp
     * atual ao nome do arquivo original.
     *
     * @param nomeArquivoOrigem
     * @return o nome do arquivo de copia criado.
     * @throws FileBackupException no caso de qualquer erro.
     */
    public String makeBackupFromFile(String nomeArquivoOrigem)
        throws FileBackupException {

        if (nomeArquivoOrigem == null) {
            throw new FileBackupException("O nome do arquivo de legenda nao pode ser nulo");
        }

        Path sourcePath = Paths.get(nomeArquivoOrigem);

        int posicaoExtensao = nomeArquivoOrigem.toLowerCase().indexOf(".srt");
        if (posicaoExtensao < 0) {
            throw new FileBackupException("O arquivo nao tem extensao .srt!");
        }

        // Vamos gerar um nome novo, baseado no timestamp atual, para o backup.
        String nomeNovoArquivo = "Backup_" + nomeArquivoOrigem.substring(0, posicaoExtensao) +
                geraComplementoTimeStamp() + ".srt";

        Path targetPath = Paths.get(nomeNovoArquivo);

        try {
            //OBS: Nao vamos sobrescrever o arquivo, pq nao deveria haver colisao.
            Files.copy(sourcePath, targetPath); // StandardCopyOption.REPLACE_EXISTING)
        } catch (IOException e) {
            throw new FileBackupException("Nao foi possivel fazer o backup de " + nomeArquivoOrigem);
        }

        return nomeNovoArquivo;
    }

    /**
     * Gera uma string no estilo "_2025-09-11_14-35-00", correspondente ao horario atual;
     * Serve para gerar nomes de arquivos unicos e informacionais.
     *
     * @return String contendo o Timestamp.
     */
    public String geraComplementoTimeStamp() {

        // Define a custom pattern
        String pattern = "_yyyy-MM-dd_HH-mm-ss";

        // Create a SimpleDateFormat object with the custom pattern
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        LocalDateTime now = LocalDateTime.now();

        return now.format(formatter);
    }

}
