/*
 * Created on Mar 29, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package tyRuBa.util;

/**
 * @author kdvolder
 */
public abstract class DelayedElementSource extends ElementSource {

    private ElementSource delayed = null;

    private ElementSource delayed() {
        if (delayed == null) {
            delayed = produce();
        }
        return delayed;
    }

    @Override
    public int status() {
        return delayed().status();
    }

    /**
     * Overide this method to provide the ElementSource the first time it is needed.
     */
    protected abstract ElementSource produce();

    @Override
    public Object nextElement() {
        return delayed().nextElement();
    }

    @Override
    public void print(PrintingState p) {
        p.print("Delayed(" + produceString());
        if (delayed != null) {
            p.indent();
            p.newline();
            delayed.print(p);
            p.outdent();
        }
        p.print(")");
    }

    protected abstract String produceString();

    /**
     * @see tyRuBa.util.ElementSource#release()
     */
    @Override
    public void release() {
        if (delayed != null) {
            // if (delayed instanceof ResultSetElementSource) {
            // System.err.println("[INFO] - release - releasing DelayedElementSource (connected to a ResultSetElementSource)");
            // } else {
            // System.err.println("[INFO] - release - releasing DelayedElementSource");
            // }
            delayed.release();
            delayed = null;
        }
    }
}
