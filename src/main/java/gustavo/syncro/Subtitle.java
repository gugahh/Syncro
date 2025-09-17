package gustavo.syncro;

import gustavo.syncro.utils.SubtitleUtil;

/**
 * Representa um item de legenda, ou seja, uma frase de um diálogo.
 */
public class Subtitle {

	private int id;
	private int startTime;
	private int endTime;
	private final StringBuilder textSb;

    // Indica a linha que contem esta legenda no arquivo .srt orginal (a linha do indice).
    private int linhaArquivoOriginal;

    // Armazena o texto contido na linha que seria o indice original da legenda.
    // Nao eh usado nos metodos de sincronizacao, apenas para validacao.
    // Para sincronizacao, utilizamos o id.
    private String textoLinhaIndice;

	public Subtitle(int id, String startTime, String endTime) {
		SubtitleUtil subtitleUtil = SubtitleUtil.getInstance();

		this.textSb = new StringBuilder();
		this.id = id;
		this.startTime	= subtitleUtil.convertSubtitleTimeStampStringToInt(startTime);
		this.endTime	= subtitleUtil.convertSubtitleTimeStampStringToInt(endTime);
	}

    public Subtitle(int id, int startTime, int endTime, int linhaArquivoOriginal, String textoLinhaIndice) {
        SubtitleUtil subtitleUtil = SubtitleUtil.getInstance();

        this.textSb = new StringBuilder();
        this.id = id;
        this.startTime	= startTime;
        this.endTime	= endTime;
        this.linhaArquivoOriginal = linhaArquivoOriginal;
        this.textoLinhaIndice = textoLinhaIndice;
    }

	public Subtitle() {
		textSb = new StringBuilder();
	}

	public int getEndTime() {
		return endTime;
	}

	public String getEndTimeAsString() {
		return SubtitleUtil.getInstance().convertIntToSubtitleTimeStamp(endTime);
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public void setEndTime(String endTime) {
		this.startTime = SubtitleUtil.getInstance().convertSubtitleTimeStampStringToInt(endTime);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStartTime() {
		return startTime;
	}

	public String getStartTimeAsString() {
		SubtitleUtil subtitleUtil = SubtitleUtil.getInstance();
		return subtitleUtil.convertIntToSubtitleTimeStamp(startTime);
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public void setStartTime(String startTime) {
		SubtitleUtil subtitleUtil = SubtitleUtil.getInstance();
		this.startTime = subtitleUtil.convertSubtitleTimeStampStringToInt(startTime);
	}

	public String getTexto() {
		return textSb.toString();
	}

	public void setTexto(String textSb) {
		this.textSb.setLength(0);
		this.textSb.append(textSb);
	}

    public int getLinhaArquivoOriginal() {
        return linhaArquivoOriginal;
    }

    public void setLinhaArquivoOriginal(int linhaArquivoOriginal) {
        this.linhaArquivoOriginal = linhaArquivoOriginal;
    }

    public String getTextoLinhaIndice() {
        return textoLinhaIndice;
    }

    public void setTextoLinhaIndice(String textoLinhaIndice) {
        this.textoLinhaIndice = textoLinhaIndice;
    }

    public void appendTexto(String texto) {
		this.textSb.append(texto);
	}

	public int hashCode() {
		return(endTime - startTime + textSb.toString().hashCode());
	}

	public boolean equals(Object obj){
		return (obj instanceof Subtitle && this.hashCode() == obj.hashCode());
	}

	@Override
	public String toString() {
		return this.getStartTimeAsString() + " > " + this.getEndTimeAsString() + ": " + this.getTexto().replace("\r\n", "<br>");
	}
}