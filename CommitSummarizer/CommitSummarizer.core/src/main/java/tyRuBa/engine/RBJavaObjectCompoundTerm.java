/*
 * Created on Aug 13, 2004
 */
package tyRuBa.engine;

import java.io.IOException;

import org.apache.regexp.RE;

import tyRuBa.engine.visitor.TermVisitor;
import tyRuBa.modes.ConstructorType;
import tyRuBa.modes.BindingMode;
import tyRuBa.modes.Factory;
import tyRuBa.modes.JavaConstructorType;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.Type;
import tyRuBa.modes.TypeConstructor;
import tyRuBa.modes.TypeEnv;
import tyRuBa.modes.TypeModeError;
import tyRuBa.util.TwoLevelKey;

public class RBJavaObjectCompoundTerm extends RBCompoundTerm {

    public static RBTerm javaClass(String name) {
        try {
            return new RBJavaObjectCompoundTerm(Class.forName(name));
        } catch (ClassNotFoundException e) {
            throw new Error("Class not found:" + name);
        }
    }

    /** An apache.regexp.RE that can be treated as a constant term */
    public static RBTerm regexp(final String re) {
        try {
            return new RBJavaObjectCompoundTerm(new RE(re) {
                private static final long serialVersionUID = 1L;

                @Override
                public String toString() {
                    return "/" + re + "/";
                }
            });
        } catch (org.apache.regexp.RESyntaxException e) {
            throw new Error("Regular expression syntax error");
        }
    }

    public static final RBTerm theEmptyList = new RBJavaObjectCompoundTerm("[]") {
        @Override
        public String quotedToString() {
            return "";
        }

        @Override
        protected Type getType(TypeEnv env) {
            return Factory.makeEmptyListType();
        }

        /**
         * This "funky" implementation of equals is because even tough we are a singleton class, we don't know for sure if some deserialization creates extra copies of me who
         * should be treated as equal.
         */
        @Override
        public boolean equals(Object obj) {
            return obj != null && obj.getClass() == this.getClass();
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }

        @Override
        public Object up() {
            return new Object[0];
        }
    };

    private Object arg;

    public RBJavaObjectCompoundTerm(Object arg) {
        if (arg instanceof String) {
            this.arg = ((String) arg).intern();
        } else {
            this.arg = arg;
        }
    }

    @Override
    public ConstructorType getConstructorType() {
        return ConstructorType.makeJava(arg.getClass());
    }

    @Override
    public RBTerm getArg() {
        return this;
    }

    @Override
    public RBTerm getArg(int i) {
        if (i == 0) {
            return this;
        } else {
            throw new Error("RBJavaObjectCompoundTerms only have one argument");
        }
    }

    @Override
    public int getNumArgs() {
        return 1;
    }

    @Override
    boolean freefor(RBVariable v) {
        return true;
    }

    @Override
    public boolean isGround() {
        return true;
    }

    @Override
    protected boolean sameForm(RBTerm other, Frame lr, Frame rl) {
        return equals(other);
    }

    @Override
    protected Type getType(TypeEnv env) throws TypeModeError {
        return ((JavaConstructorType) getConstructorType()).getType();
    }

    @Override
    public int formHashCode() {
        return 9595 + arg.hashCode();
    }

    @Override
    public int hashCode() {
        return 9595 + arg.hashCode();
    }

    @Override
    public BindingMode getBindingMode(ModeCheckContext context) {
        return Factory.makeBound();
    }

    @Override
    public void makeAllBound(ModeCheckContext context) {
    }

    @Override
    public boolean equals(Object x) {
        if (x.getClass().equals(this.getClass())) {
            return arg.equals(((RBJavaObjectCompoundTerm) x).arg);
        } else {
            return false;
        }
    }

    @Override
    public Object accept(TermVisitor v) {
        return this;
    }

    @Override
    public boolean isOfType(TypeConstructor t) {
        return t.isSuperTypeOf(getTypeConstructor());
    }

    @Override
    public Object up() {
        return arg;
    }

    @Override
    public Frame unify(RBTerm other, Frame f) {
        if (other instanceof RBVariable)
            return other.unify(this, f);
        else if (equals(other))
            return f;
        else
            return null;
    }

    /**
     * @see tyRuBa.util.TwoLevelKey#getFirst()
     */
    @Override
    public String getFirst() {
        if (arg instanceof String) {
            String str = (String) arg;
            int firstindexofhash = str.indexOf('#');
            if (firstindexofhash == -1) {
                return " ";
            } else {
                return str.substring(0, firstindexofhash).intern();
            }
        } else if (arg instanceof Number) {
            return ((Number) arg).toString();
        } else if (arg instanceof TwoLevelKey) {
            return ((TwoLevelKey) arg).getFirst();
        } else {
            throw new Error("This object does not support TwoLevelKey indexing: " + arg);
        }
    }

    /**
     * @see tyRuBa.util.TwoLevelKey#getSecond()
     */
    @Override
    public Object getSecond() {
        if (arg instanceof String) {
            String str = (String) arg;
            int firstindexofhash = str.indexOf('#');
            if (firstindexofhash == -1) {
                return str;
            } else {
                return str.substring(firstindexofhash).intern();
            }
        } else if (arg instanceof Number) {
            return ((Number) arg).toString();
        } else if (arg instanceof TwoLevelKey) {
            return ((TwoLevelKey) arg).getSecond();
        } else {
            throw new Error("This object does not support TwoLevelKey indexing: " + arg);
        }
    }

    @Override
    public String toString() {
        return arg.toString();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (arg instanceof String) {
            arg = ((String) arg).intern();
        }
    }

    @Override
    public int intValue() {
        if (arg instanceof Integer) {
            return ((Integer) arg).intValue();
        } else {
            return super.intValue();
        }
    }

    public Object getObject() {
        return this.arg;
    }

}
