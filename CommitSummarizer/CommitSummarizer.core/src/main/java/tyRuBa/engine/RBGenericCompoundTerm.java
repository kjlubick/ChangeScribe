/*
 * Created on Aug 13, 2004
 */
package tyRuBa.engine;

import tyRuBa.modes.ConstructorType;

/**
 * @author riecken
 */
public class RBGenericCompoundTerm extends RBCompoundTerm {

    ConstructorType typeTag;

    RBTerm args;

    @Override
    public RBTerm getArg() {
        return args;
    }

    public RBGenericCompoundTerm(ConstructorType constructorType, RBTerm args) {
        this.typeTag = constructorType;
        this.args = args;
    }

    @Override
    public ConstructorType getConstructorType() {
        return typeTag;
    }

}