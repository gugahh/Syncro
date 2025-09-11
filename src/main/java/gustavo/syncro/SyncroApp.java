package gustavo.syncro;

import gustavo.syncro.actions.RenumerarAction;
import gustavo.syncro.actions.TimeAdjustAction;
import gustavo.syncro.actions.SplitAction;
import gustavo.syncro.utils.*;

import java.io.*;

public class SyncroApp {

	private static final TimeAdjustAction timeAdjustAction = TimeAdjustAction.getInstance();

	private static final RenumerarAction renumerarAction = RenumerarAction.getInstance();

    private static final SplitAction splitAction = SplitAction.getInstance();


	public SyncroApp(){
	}

	public static void main(String[] args) throws IOException {

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
			System.out.println("\tO parametro de entrada nao foi compreendido.");
			System.out.println("\tConsulte o Help para selecionar a operacao desejada.");
			System.out.println(HelpUtil.howToGetHelpStr);
			System.exit(0);
		}

        // Se chegou aqui, temos um numero de parametros possivelmente OK.
        // Repassando o processamento para uma das actions disponiveis
        switch(args[0].toUpperCase()) {
            case "-ADJUST" :
                timeAdjustAction.doAction(args);
                break;
            case "-RENUM" :
                renumerarAction.doAction(args);
                break;
            case "-SPLIT" :
                splitAction.doAction(args);
                break;

            default:
                System.out.println("Parametro Invalido.");
                System.out.println(HelpUtil.howToGetHelpStr);
                System.exit(0);
        }

	}//main

}
