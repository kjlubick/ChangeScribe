package tyRuBa.engine;

import tyRuBa.engine.visitor.TermVisitor;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;

public class RBQuoted extends RBAbstractPair {

    private static final RBTerm quotedName = FrontEnd.makeName("{}");

    public RBQuoted(RBTerm listOfParts) {
        super(quotedName, listOfParts);
    }

    @Override
    public Object up() {
        return quotedToString();
    }

    @Override
    public String toString() {
        return "{" + getQuotedParts().quotedToString() + "}";
    }

    @Override
    public String quotedToString() {
        return getQuotedParts().quotedToString();
    }

    public RBTerm getQuotedParts() {
        return getCdr();
    }

    @Override
    protected Type getType(TypeEnv env) throws TypeModeError {
        return Factory.makeSubAtomicType(Factory.makeTypeConstructor(String.class));
    }

    @Override
    public Object accept(TermVisitor v) {
        return v.visit(this);
    }

    /**
     * @see tyRuBa.util.TwoLevelKey#getFirst()
     */
    @Override
    public String getFirst() {
        return getCdr().getFirst();
    }

    /**
     * @see tyRuBa.util.TwoLevelKey#getSecond()
     */
    @Override
    public Object getSecond() {
        return getCdr().getSecond();
    }
}
