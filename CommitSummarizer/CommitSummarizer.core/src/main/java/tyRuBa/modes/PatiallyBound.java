package tyRuBa.modes;

public class PatiallyBound extends BindingMode {

    static public PatiallyBound the = new PatiallyBound();

    private PatiallyBound() {
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PatiallyBound;
    }

    @Override
    public String toString() {
        return "BF";
    }

    /** check that this binding satisfied the binding mode */
    @Override
    public boolean satisfyBinding(BindingMode mode) {
        return mode instanceof Free;
    }

    @Override
    public boolean isBound() {
        return false;
    }

    @Override
    public boolean isFree() {
        return false;
    }
}
