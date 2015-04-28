/*
 * Created on Aug 19, 2004
 */
package tyRuBa.modes;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import tyRuBa.engine.FunctorIdentifier;
import tyRuBa.engine.RBCompoundTerm;
import tyRuBa.engine.RBJavaObjectCompoundTerm;
import tyRuBa.engine.RBTerm;

/**
 * @author riecken
 */
public class JavaConstructorType extends ConstructorType {
	
	Class javaClass;
	
	public JavaConstructorType(Class javaClass) {
		this.javaClass = javaClass;
	}
	
	@Override
    public RBTerm apply(ArrayList terms) {
		throw new Error("Java Constructors can only be applied a single term");
	}
	
	@Override
    public RBTerm apply(RBTerm term) {
		if (term instanceof RBJavaObjectCompoundTerm) {
			RBJavaObjectCompoundTerm java_term = (RBJavaObjectCompoundTerm) term;
			if (this.getTypeConst().isSuperTypeOf(java_term.getTypeConstructor()))
				return java_term;
			else {
				Object obj = java_term.getObject();
				try {
					Constructor ctor = javaClass.getConstructor(new Class[] {obj.getClass()});
					return RBCompoundTerm.makeJava(ctor.newInstance(new Object[] {obj}));
				} catch (Exception e) {
					throw new Error("Illegal TyRuBa to Java Type Cast: "+java_term+"::"+this);
					// TODO: This is really a TypeModeError 
				}
			}
		}
		return RBCompoundTerm.make(this,term);
	}
	
	@Override
    public Type apply(Type argType) throws TypeModeError {
		Type iresult = getType();
		argType.checkEqualTypes(iresult);
		return iresult;
	}
	
	public Type getType() {
		return Factory.makeSubAtomicType(this.getTypeConst());
	}
	
	@Override
    public boolean equals(Object other) {
		if (this.getClass()!=other.getClass())
			return false;
		else
			return this.javaClass.equals(((JavaConstructorType)other).javaClass);
	}
	
	@Override
    public int hashCode() {
		return javaClass.hashCode();
	}
	
	@Override
    public int getArity() {
		return 1;
	}
	
	@Override
    public FunctorIdentifier getFunctorId() {
		return new FunctorIdentifier(javaClass.getName(),1);
	}
	@Override
    public TypeConstructor getTypeConst() {
		return Factory.makeTypeConstructor(javaClass);
	}
	
	@Override
    public String toString() {
		return "JavaConstructorType("+javaClass+")";
	}
}
