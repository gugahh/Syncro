package gustavo.syncro;

public class Subtitle {

	private int id;
	private int startTime;
	private int endTime;
	private StringBuilder texto;

	public Subtitle(int id, int startTime, int endTime) {
		this.texto = new StringBuilder("");
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Subtitle(int id, String startTime, String endTime) {
		this.texto = new StringBuilder("");
		this.id = id;
		this.startTime	= convertSubtitleTimeStampStringToInt(startTime);
		this.endTime	= convertSubtitleTimeStampStringToInt(endTime);
	}


	public Subtitle() {
		texto = new StringBuilder("");
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String entrada = "18:58:03,996";
		System.out.println("Entrada: " + entrada);
		int d = convertSubtitleTimeStampStringToInt(entrada);
		System.out.println(d);
		System.out.println("-----------------------");
		System.out.println(convertIntToSubtitleTimeStamp(d));
	}

	public int getEndTime() {
		return endTime;
	}

	public String getEndTimeAsString() {
		return convertIntToSubtitleTimeStamp(endTime);
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public void setEndTime(String endTime) {
		this.startTime = convertSubtitleTimeStampStringToInt(endTime);
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
		return convertIntToSubtitleTimeStamp(startTime);
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = convertSubtitleTimeStampStringToInt(startTime);
	}

	public String getTexto() {
		return texto.toString();
	}

	public void setTexto(String texto) {
		this.texto = new StringBuilder(texto);
	}

	public void appendTexto(String texto) {
		this.texto.append(texto);
	}

	//transforma uma String contendo um período no formato 00:00:01,520 para um int.
	public static int convertSubtitleTimeStampStringToInt(String subtitleMillisString){
		int horas		= Integer.parseInt(subtitleMillisString.substring(0, 2));
		int minutos		= Integer.parseInt(subtitleMillisString.substring(3, 5));
		int segundos	= Integer.parseInt(subtitleMillisString.substring(6, 8));
		int milis		= Integer.parseInt(subtitleMillisString.substring(9, 12));

		/*
		System.out.println("Horas: " + horas);
		System.out.println("Minutos: " + minutos);
		System.out.println("Segundos: " + segundos);
		System.out.println("Milis: " + milis); */

		return ((horas*1000*3600)+(minutos*1000*60)+(segundos*1000)+milis);
	}

	//faz o inverso: transforma um inteiro em uma String contendo um período no formato 00:00:01,520
	public static String convertIntToSubtitleTimeStamp(int timeStampAsInt){
		int horas		= (int)  timeStampAsInt / (1000*3600);
		int minutos		= (int) (timeStampAsInt % (1000*3600)) / 60000;
		int segundos	= (int) (timeStampAsInt % 60000) / 1000;
		int milis		= (int) timeStampAsInt % 1000;
		StringBuilder out = new StringBuilder("");
		out.append(String.format("%02d", horas) + ":");
		out.append(String.format("%02d", minutos) + ":");
		out.append(String.format("%02d", segundos) + ",");
		out.append(String.format("%03d", milis));

		return out.toString();
	}

	public int hashCode() {
		return(endTime - startTime + texto.toString().hashCode());
	}


	public boolean equals(Object obj){
		if(obj instanceof Subtitle && this.hashCode() == obj.hashCode()) {
			return true;
		}
		return false;
	}


	@Override
	public String toString() {
		return this.getStartTimeAsString() + " > " + this.getEndTimeAsString() + ": " + this.getTexto().replace("\r\n", "<br>");
	}
}