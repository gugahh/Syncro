package gustavo.syncro.strategy;

import gustavo.syncro.enums.ExecOutcome;

/**
 * Assinatura (e implementacoes genericas) que todas as funcoes
 * que o Syncro executa devem seguir.
 * Cada função (ex: Adjust) tera o seu arquivo separado,
 * realizando tambem todos os testes de parametros necessarios.
 */
public abstract class AbstractStrategy {

    public abstract ExecOutcome execute(String[] params);
}
