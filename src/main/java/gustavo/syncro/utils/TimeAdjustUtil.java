package gustavo.syncro.utils;

import gustavo.syncro.actions.RenumerarAction;

public class TimeAdjustUtil {

    private static final TimeAdjustUtil instance = new TimeAdjustUtil();

    // private constructor to avoid client applications using the constructor
    private TimeAdjustUtil(){}

    public static TimeAdjustUtil getInstance() {
        return instance;
    }
}
