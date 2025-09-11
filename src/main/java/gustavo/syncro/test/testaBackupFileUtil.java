package gustavo.syncro.test;

import gustavo.syncro.exceptions.validacao.FileBackupException;
import gustavo.syncro.utils.BackupFileUtil;

public class testaBackupFileUtil {

    public static void main(String[] args) throws FileBackupException {

        BackupFileUtil bfu = new BackupFileUtil();

        // Teste 1: geraComplementoTimeStamp
        System.out.println("Complemento:" + bfu.geraComplementoTimeStamp());

        // Teste 2: Gera uma copia de heroes_sample.srt
        // (verificar no sistema de arquivos.
        String nomeNovoArquivo = bfu.makeBackupFromFile("heroes_sample.srt");
        System.out.println("Nome do arquivo gerado: " + nomeNovoArquivo);
    }
}
