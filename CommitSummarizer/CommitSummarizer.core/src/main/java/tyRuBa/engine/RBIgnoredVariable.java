package tyRuBa.engine;

import java.io.ObjectStreamException;

import tyRuBa.engine.visitor.TermVisitor;
import tyRuBa.modes.Factory;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeEnv;

/** A variable who's binding is totally ignored like prologs _ variable */
public class RBIgnoredVariable extends RBVariable {

	public static final RBIgnoredVariable the = new RBIgnoredVariable();

	private RBIgnoredVariable() {
		super("?");
	}

	@Override
    public Frame unify(RBTerm other, Frame f) {
		return f;
	}

	@Override
    boolean freefor(RBVariable v) {
		return true;
	}

	@Override
    protected boolean sameForm(RBTerm other, Frame lr, Frame rl) {
		return this == other;
	}

	@Override
    public int formHashCode() {
		return 1;
	}

	@Override
    public boolean equals(Object obj) {
		return obj == this;
	}

	@Override
    public int hashCode() {
		return 66727982;
	}

	@Override
    public Object clone() {
		return this;
	}
	
	@Override
    protected Type getType(TypeEnv env) {
		return Factory.makeTVar("");
	}
	
	@Override
    public Object accept(TermVisitor v) {
		return v.visit(this);
	}

	public Object readResolve() throws ObjectStreamException {
		return the; // this is a singleton class, should not allow
		            // creation of copies, not even by deserialization
	}
	
}
