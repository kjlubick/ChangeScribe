/*****************************************************************\
 * File:        Free.java
 * Author:      TyRuBa
 * Meta author: Kris De Volder <kdvolder@cs.ubc.ca>
\*****************************************************************/
package tyRuBa.modes;

public class Free extends BindingMode {

    static public Free the = new Free();

    private Free() {
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Free;
    }

    @Override
    public String toString() {
        return "F";
    }

    /** check that this binding satisfied the binding mode */
    @Override
    public boolean satisfyBinding(BindingMode mode) {
        return this.equals(mode);
    }

    @Override
    public boolean isBound() {
        return false;
    }

    @Override
    public boolean isFree() {
        return true;
    }
}
