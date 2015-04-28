package tyRuBa.modes;

import java.io.Serializable;

public class JavaTypeConstructor extends TypeConstructor implements Serializable {

	private final Class javaClass;

	/** Constructor */
	public JavaTypeConstructor(Class javaclass) {
		if (javaclass.isInterface() || javaclass.isPrimitive()) {
			throw new Error("no interfaces or primitives types are allowed");
		} else {
			this.javaClass = javaclass;
		}
	}
	
	@Override
    public String getName() {
		String name = javaClass.getName();
		if (name.startsWith("java.lang.")) {
			return name.substring("java.lang.".length());
		} else {
			return name;
		}
	}

	@Override
    public boolean equals(Object other) {
		if (other != null && this.getClass().equals(other.getClass())) {
			return this.getName().equals(((TypeConstructor)other).getName());
		} else {
			return false;
		}
	}

	@Override
    public int hashCode() {
		return getName().hashCode();
	}

	public void addSuperType(TypeConstructor superType) throws TypeModeError {
		throw new TypeModeError("Can not add super type for java types " + this);
	}

    @Override
    public TypeConstructor getSuperTypeConstructor() {
        if (javaClass.getSuperclass() == null)
            return null;
        else
            return new JavaTypeConstructor(javaClass.getSuperclass());
    }

    @Override
    public int getTypeArity() {
        return 0;
    }

    @Override
    public String getParameterName(int i) {
        throw new Error("This is not a user defined type");
    }

    /* (non-Javadoc)
     * @see tyRuBa.modes.TypeConstructor#isInitialized()
     */
    @Override
    public boolean isInitialized() {
        throw new Error("This is not a user defined type");
    }

    @Override
    public ConstructorType getConstructorType() {
        return ConstructorType.makeJava(javaClass);
    }
    
    @Override
    public boolean isJavaTypeConstructor() {
        return true;
    }

    @Override
    public String toString() {
    		return "JavaTypeConstructor("+javaClass+")";
    }
    
	@Override
    public Class javaEquivalent() {
		return javaClass;
	}
}
