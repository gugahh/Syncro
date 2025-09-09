package gustavo.syncro;

import gustavo.syncro.actions.RenumerarAction;
import gustavo.syncro.utils.*;
import gustavo.syncro.actions.TimeAdjustAction;

import java.io.*;

public class SyncroApp {

	private static final TimeAdjustAction timeAdjustAction = TimeAdjustAction.getInstance();

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

        // Se chegou aqui, temos um numero de parametros possivelmente OK.
        // Repassando o processamento para uma das actions disponiveis
        switch(args[0].toUpperCase()) {
            case "-ADJUST" :
                timeAdjustAction.doAction(args);
                break;
            case "-RENUM" :
                renumerarAction.doAction(args);
                break;
            default:
                System.out.println("Parametro Invalido.");
                System.out.println(HelpUtil.howToGetHelpStr);
                System.exit(0);
        }

	}//main

}
