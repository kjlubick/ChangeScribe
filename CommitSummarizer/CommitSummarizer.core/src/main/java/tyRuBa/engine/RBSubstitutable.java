package tyRuBa.engine;

/**
 * @author kdvolder
 */
public abstract class RBSubstitutable extends RBTerm {

    protected String name;

    RBSubstitutable(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof RBSubstitutable)
                && ((RBSubstitutable) obj).name == this.name;
    }

}
