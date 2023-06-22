package gustavo.synchro;

import java.io.IOException;

public class TestaSynchro {

	enum TipoTeste {
		ADJUST_TIME_NO_INDEX (
				"-adjust",
				"monk.srt",
				"-00:01s",	//arg2: tempoAjuste
				null,		//arg3: LegendaInicial
				null		//Arg4
		),
		ADJUST_TIME_INDEX (
				"-adjust",
				"heroes.srt",
				"12:25s",	//arg2: tempoAjuste
				"2",		//arg3: LegendaInicial
				null		//Arg4
		),
		RENUM(
				"-renum",
				"monk.srt",
				"634",		//arg2: Posic inicial para renumerar (vai daqui até o fim).
				"1",		//arg3: Posic desejada após renumerar
				null		//Arg4
		),
		RENUM_INVALID_ARGUMENT_1(
				"-renum",
				"monk.srt",
				"jota",		//arg2: Posic inicial para renumerar (vai daqui até o fim): Deve acusar erro.
				"10",		//arg3: Posic desejada após renumerar
				null		//Arg4
		),
		RENUM_INVALID_ARGUMENT_2(
				"-renum",
				"monk.srt",
				"10",		//arg2: Posic inicial para renumerar (vai daqui até o fim): Deve acusar erro.
				"-1",		//arg3: Posic desejada após renumerar
				null		//Arg4
		);


		TipoTeste(String acao, String arquivoLegenda, String arg2, String arg3, String arg4) {
			this.acao=acao;
			this.arquivoLegenda=arquivoLegenda;
			this.arg2=arg2;
			this.arg3=arg3;
			this.arg4=arg4;
		}

		public final String acao;
		public final String arquivoLegenda;
		public final String arg2;
		public final String arg3;
		public final String arg4;
	}


	public static void main(String[] args) throws IOException {

		//Indique aqui o teste que deseja executar
		final TipoTeste test = TipoTeste.ADJUST_TIME_NO_INDEX;

		// Passando apenas os parâmetros que se aplicam.
		if(test.arg3==null){
			SynchroApp.main( new String[]{test.acao, test.arquivoLegenda, test.arg2 } );
		}
		if(test.arg3!=null && test.arg4==null){
			SynchroApp.main( new String[]{test.acao, test.arquivoLegenda, test.arg2, test.arg3} );
		}
		if(test.arg4!=null){
			SynchroApp.main( new String[]{test.acao, test.arquivoLegenda, test.arg2, test.arg3, test.arg4} );
		}
	} //main
}
