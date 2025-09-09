package gustavo.syncro;

import gustavo.syncro.actions.RenumerarAction;
import gustavo.syncro.exceptions.FileReadException;
import gustavo.syncro.exceptions.ArquivoLegendaWriteException;
import gustavo.syncro.exceptions.PosicaoLegendaInvalidaException;
import gustavo.syncro.exceptions.validacao.ValidacaoException;
import gustavo.syncro.utils.*;
import gustavo.syncro.actions.TimeAdjustAction;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SyncroApp {


	private static final SubtitleUtil sbtUtil = SubtitleUtil.getInstance();

	private static final TimeConversionUtil timeConversionUtil = TimeConversionUtil.getInstance();

	private static final TimeAdjustAction TIME_ADJUST_ACTION = TimeAdjustAction.getInstance();

	private static final RenumerarAction renumerarAction = RenumerarAction.getInstance();

	public SyncroApp(){
	}


	public static void main(String[] args) throws IOException {

        // TODO: tratar o Backup.
        boolean fazerBackupLegenda = true;

		if(args.length == 0){
			//Usuário não passou nenhum parâmetro. Exibir help básico:
			SyncroHelp.printBasicHelp();
			System.exit(0);
		}

		// TODO: Usar regex para simplificar abaixo.
		if(args.length > 0 &&
				(args[0].equalsIgnoreCase("-help") || 
						args[0].equalsIgnoreCase("-h") ||
						args[0].equalsIgnoreCase("/help") ||
						args[0].equalsIgnoreCase("/h"))
		   ){
			//Usu solicitou um help estendido.
			SyncroHelp.printExtendedHelp();
			System.exit(0);
		}

		if(args.length == 1){ //Usu quer algum tipo de alteracao, mas nao informou arquivo
			System.out.println("\tO parametro arquivo (de legenda) e obrigatorio");
			System.out.println("\tpara realizar qualquer operacao.");
			System.out.println(HelpUtil.howToGetHelpStr);
			System.exit(0);
		}

		if(args[0].equalsIgnoreCase("-adjust")){

			/* Usuario solicitou ajustar legenda.
			 * Testando consistência de parâmetros de entrada */

			if(args.length == 2){ //Usu informou arquivo, mas nao o tempo de ajuste
				System.out.println("\tO parametro tempo (de ajuste) e obrigatorio");
				System.out.println("\tpara realizar esta operacao.");
				System.out.println(HelpUtil.howToGetHelpStr);
				System.exit(0);
			}

			if(args.length >= 3){
				/* Nº de params e legal. Pode-se tentar comecar.
				 * No caso de qualquer método falhar a operação
				 * (execução da App) deve ser abortada. */

				// args[0] args[1]   args[2]  args[3](opc)     args[3 ou 4] (opc)
				//[-adjust [arquivo] [tempo] [indiceLegenda] ] [-nobak]

				String indiceLegendaInicial = null; //default caso usuário não passe uma referência de índice

				if(args.length >= 4){
					//Args[3] pode ser um índice de legenda (opc) ou -nobak.
					if(args[3].equalsIgnoreCase("-nobak") ||
							args[3].equalsIgnoreCase("-noback")){ //Usu solicitou não fazer backup
                        fazerBackupLegenda = false;
                        System.out.println("Fazer Backup - falta implementar");
					} else indiceLegendaInicial = args[3];
				}

				if(args.length == 5){ //O quarto parametro SÓ PODE ser referente ao Backup.
					if(args[4].equalsIgnoreCase("-nobak")){ //Usu solicitou não fazer backup
                        fazerBackupLegenda = false;
                        System.out.println("Fazer Backup - falta implementar");
					} else { //Parâmetro inválido (deveria ser -nobak, ou não existir).
						System.out.println("\tParametro invalido / desconhecido.");
						System.out.println(HelpUtil.howToGetHelpStr);
						System.exit(-1);
					}
				}

				/* Nº de parâmetros passados e ordem é correta.
				 * Testando a consistência de cada um dos parâmetros. */
				String msgTesteAjuste = TIME_ADJUST_ACTION.testaParamsEntradaAdjust(args[1], args[2], indiceLegendaInicial); //Caso passe, tudo Ok.
				if (msgTesteAjuste != null) {
					System.out.println(msgTesteAjuste);
					System.out.println(HelpUtil.howToGetHelpStr);
					System.exit(-1);
				}

                List<Subtitle> listaLegendas = null;
                try {
                    listaLegendas = sbtUtil.obtemListaLegendasFromFile(args[1]);
                } catch (ValidacaoException e) {
                    throw new RuntimeException(e);
                }

                if (null == listaLegendas || listaLegendas.isEmpty()) {
                    throw new RuntimeException("Lista de Legendas eh nula ou vazia.");
                }

                //Efetua alterações de tempo solicitadas.
				int indicelegendaInt = 1;
				//caso o Usu tenha informado um valor válido de em qual legenda iniciar, utilizá-lo.
				if(indiceLegendaInicial!= null) indicelegendaInt = Integer.parseInt(indiceLegendaInicial);

				try {
					TIME_ADJUST_ACTION.modificaTempoTodasLegendas(listaLegendas,
							timeConversionUtil.getMillisFromUserString(args[2]),
							indicelegendaInt
							);
					// s.modifytime(timeConversionUtil.getMillisFromUserString(args[2]), indicelegendaInt);
				} catch (PosicaoLegendaInvalidaException plie) {
					plie.printStackTrace();
					System.exit(-1);
				}

				//Salva as alterações no arquivo de origem.
				// s.saveChangedSubtitleFile(args[1]);
				try {
					SubtitleFileUtil.saveChangedSubtitleFile(args[1], listaLegendas);
				} catch (ArquivoLegendaWriteException e) {
					System.out.println(e.getMessage());
					System.out.println(HelpUtil.howToGetHelpStr);
					System.exit(-1);
				}

				System.out.println("O Tempo das Legendas foi ajustado com sucesso.");
			}
		} else {
			if(args[0].equalsIgnoreCase("-renum")) { //Usuário tenta renumerar legendas

				if(args.length<4){ //Num de params menor que o esperado.
					System.out.println("\tNumero de parametros incorreto");
					System.out.println("\tpara realizar esta operacao.");
					System.out.println(HelpUtil.howToGetHelpStr);
					System.exit(0);
				}

				if(args.length >= 4){
					/* Nº de params é legal. Pode-se tentar comecar.
					 * No caso de qualquer método falhar a operação
					 * (execução da App) deve ser abortada. */

					// args[0] args[1]   args[2]         args[3]           args[4] (opc)
					//[-renum [arquivo] [indiceInicial] [renumerarPara] ] [-nobak]

					String indiceLegendaInicial = null; //default caso usuário não passe uma referência de índice

					if(args.length == 5){
						//Args[4] só pode ser -nobak.
						if(args[4].equalsIgnoreCase("-nobak")){ //Usu solicitou não fazer backup
                            fazerBackupLegenda = false;
                            System.out.println("Fazer Backup - falta implementar");
						} else {
							//Parâmetro inválido. Unica opção é -nobak.
							System.out.println("\tparametro incorreto");
							System.out.println(HelpUtil.howToGetHelpStr);
							System.exit(-1);
						}
					}

					/* Nº de parâmetros passados e ordem é correta.
					 * Testando a consistência de cada um dos parâmetros. */
					String retornoTestaRenumerar = renumerarAction.testaParamsEntradaRenum(args[1], args[2], args[3]);
					if (retornoTestaRenumerar != null) {
						System.out.println(retornoTestaRenumerar);
						System.out.println(HelpUtil.howToGetHelpStr);
						System.exit(-1);
					}

                    List<Subtitle> listaLegendas = null;
                    try {
                        listaLegendas = sbtUtil.obtemListaLegendasFromFile(args[1]);
                    } catch (ValidacaoException e) {
                        throw new RuntimeException(e);
                    }

                    if (null == listaLegendas || listaLegendas.isEmpty()) {
                        throw new RuntimeException("Lista de Legendas eh nula ou vazia.");
                    }

                    renumerarAction.modifyIndiceLegendas(listaLegendas, args[2], args[3]);

					//Salva as alterações no arquivo de origem.
					// s.saveChangedSubtitleFile(args[1]);
					try {
						SubtitleFileUtil.saveChangedSubtitleFile(args[1], listaLegendas);
					} catch (ArquivoLegendaWriteException e) {
						System.out.println(e.getMessage());
						System.out.println(HelpUtil.howToGetHelpStr);
						System.exit(-1);
					}

					System.out.println("O Índice das Legendas foi ajustado com sucesso.");

				}//4 params
			} else {
				// Caso não se esteja ajustando tempo, num legenda OU solicitando help, param é invalido.
				System.out.println("Parametro Invalido.");
				System.out.println(HelpUtil.howToGetHelpStr);
				System.exit(0);
			}
		} //-adjust
	}//main

}
