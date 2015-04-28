package tyRuBa.modes;

import java.io.Serializable;

import tyRuBa.engine.MetaBase;

public class UserDefinedTypeConstructor extends TypeConstructor implements Serializable {

    private String name;

    private TypeConstructor superConst = null;

    private Type representedBy;

    private TVar[] parameters;

    boolean initialized = false;

    private ConstructorType constructorType;

    private TypeMapping mapping;

    /**
     * @codegroup metadata
     */
    private transient MetaBase metaBase = null;

    /**
     * @codegroup metadata
     */
    @Override
    public void setMetaBase(MetaBase metaBase) {
        this.metaBase = metaBase;
        if (superConst != null) {
            metaBase.assertSubtype(superConst, this);
        }
    }

    /** Constructor for types that have no supertypes */
    public UserDefinedTypeConstructor(String name, int arity) {
        this.name = name;
        this.parameters = new TVar[arity];
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof UserDefinedTypeConstructor)) {
            return false;
        } else {
            UserDefinedTypeConstructor tother = (UserDefinedTypeConstructor) other;
            return name.equals(tother.name)
                    && parameters.length == tother.parameters.length;
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 13 + parameters.length;
    }

    @Override
    public void addSubTypeConst(TypeConstructor subConst) throws TypeModeError {
        subConst.addSuperTypeConst(this);
    }

    /**
     * @codegroup metadata
     */
    @Override
    public void addSuperTypeConst(TypeConstructor superConst) throws TypeModeError {
        if (this.equals(superConst)) {
            throw new TypeModeError(
                    "Recursion in type inheritance: " + this + " depends on itself");
        } else {
            if (this.superConst == null) {
                this.superConst = superConst;
                if (metaBase != null)
                    metaBase.assertSubtype(superConst, this);
            } else {
                throw new TypeModeError("Multiple inheritance not supported: " + this
                        + " inherits from " + this.superConst + " and " + superConst);
            }
        }
    }

    /**
     * @codegroup metadata
     */
    @Override
    public void setRepresentationType(Type repType) {
        representedBy = repType;
        if (metaBase != null)
            metaBase.assertRepresentation(this, repType);
    }

    @Override
    public TypeConstructor getSuperTypeConstructor() {
        if (superConst != null)
            return superConst;
        else
            return TypeConstructor.theAny;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getTypeArity() {
        return parameters.length;
    }

    /** Argument is assumed to be a tuple of TVars */
    @Override
    public void setParameter(TupleType type) {
        if (type.size() != this.getTypeArity())
            throw new Error("This should not happen");
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = (TVar) type.get(i);
        }
        initialized = true;
    }

    @Override
    public String getParameterName(int i) {
        if (i < getTypeArity()) {
            return parameters[i].getName();
        } else {
            throw new Error("This should not happen");
        }
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(name + "(");
        for (int i = 0; i < getTypeArity(); i++) {
            if (i > 0) {
                result.append(",");
            }
            result.append(parameters[i]);
        }
        result.append(")");
        if (representedBy != null) {
            result.append(" AS " + representedBy);
        }
        return result.toString();
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public Type getRepresentation() {
        return representedBy;
    }

    @Override
    public boolean hasRepresentation() {
        return representedBy != null;
    }

    @Override
    public void setConstructorType(ConstructorType constrType) {
        if (constructorType != null)
            throw new Error("Should not set twice!");
        if (!hasRepresentation())
            throw new Error("Only concrete composite types can have a constructorType");
        this.constructorType = constrType;
    }

    @Override
    public ConstructorType getConstructorType() {
        return constructorType;
    }

    @Override
    public TypeConstructor getSuperestTypeConstructor() {
        TypeConstructor result = super.getSuperestTypeConstructor();
        if (TypeConstructor.theAny.equals(result))
            return this;
        else
            return result;
    }

    public TypeMapping getMapping() {
        return mapping;
    }

    public void setMapping(TypeMapping mapping) {
        if (this.mapping != null)
            throw new Error("Can only define a single Java type mapping per tyRuBa type");
        else
            this.mapping = mapping;
    }

    @Override
    public Class javaEquivalent() {
        if (getMapping() == null)
            return null;
        else
            return getMapping().getMappedClass();
    }

}
