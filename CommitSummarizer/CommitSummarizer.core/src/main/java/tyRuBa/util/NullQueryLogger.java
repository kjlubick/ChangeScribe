/*
 * Created on May 16, 2005
 */
package tyRuBa.util;

import tyRuBa.engine.RBExpression;

/**
 * @author kdvolder
 */
public class NullQueryLogger extends QueryLogger {

    public static NullQueryLogger the = new NullQueryLogger();

    private NullQueryLogger() {
    }

    @Override
    final public void close() {
    }

    @Override
    final public void logQuery(RBExpression query) {
    }

}
