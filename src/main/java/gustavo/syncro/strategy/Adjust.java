package gustavo.syncro.strategy;

import gustavo.syncro.enums.ExecOutcome;

public class Adjust extends AbstractStrategy {


    @Override
    public ExecOutcome execute(String[] params) {
        return ExecOutcome.FAIL;
    }
}
