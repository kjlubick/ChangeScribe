/*****************************************************************\
 * File:        Bound.java
 * Author:      TyRuBa
 * Meta author: Kris De Volder <kdvolder@cs.ubc.ca>
\*****************************************************************/
package tyRuBa.modes;

public class Bound extends BindingMode {
	
	static public Bound the = new Bound();

	private Bound() {}

	@Override
    public int hashCode() {
		return this.getClass().hashCode();
	}

	@Override
    public boolean equals(Object other) {
		return other instanceof Bound;
	}

	@Override
    public String toString() {
		return "B";
	}

	@Override
    public boolean satisfyBinding(BindingMode mode) {
		return true;
	}

	@Override
    public boolean isBound() {
		return true;
	}
	@Override
    public boolean isFree() {
		return false;
	}
	
}
