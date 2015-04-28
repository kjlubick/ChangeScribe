package tyRuBa.engine;

import tyRuBa.engine.compilation.CompilationContext;
import tyRuBa.engine.compilation.Compiled;
import tyRuBa.modes.Mode;
import tyRuBa.modes.ModeCheckContext;
import tyRuBa.modes.PredInfoProvider;
import tyRuBa.modes.PredicateMode;
import tyRuBa.modes.TupleType;
import tyRuBa.modes.TypeModeError;

/**
 * A ValidatorComponent is an RBComponent wrapping an other RBComponent "guarding" it with a Validator. The Validator may at any time become invalidated. At this time the Validator
 * itself becomes invalidated and may be removed from wherever it happens to be stored.
 * 
 * @author kdvolder
 */

public class ValidatorComponent extends RBComponent {

    private Validator validator;

    private RBComponent comp;

    public ValidatorComponent(RBComponent c, Validator validator) {
        this.validator = validator;
        this.comp = c;
    }

    @Override
    public boolean isValid() {
        return (validator != null && validator.isValid());
    }

    void checkValid() {
        if (!isValid()) {
            throw new Error("Internal Error: Using an invalidated component: "
                    + this);
        }
    }

    @Override
    public Validator getValidator() {
        return validator;
    }

    @Override
    public RBTuple getArgs() {
        checkValid();
        return comp.getArgs();
    }

    @Override
    public PredicateIdentifier getPredId() {
        checkValid();
        return comp.getPredId();
    }

    @Override
    public TupleType typecheck(PredInfoProvider predinfos) throws TypeModeError {
        checkValid();
        return comp.typecheck(predinfos);
    }

    @Override
    public RBComponent convertToNormalForm() {
        checkValid();
        return new ValidatorComponent(comp.convertToNormalForm(), validator);
    }

    @Override
    public boolean isGroundFact() {
        checkValid();
        return comp.isGroundFact();
    }

    @Override
    public String toString() {
        if (isValid())
            return comp.toString();
        else
            return "ValidatorComponent(INVALIDATED," + comp + ")";
    }

    @Override
    public RBComponent convertToMode(PredicateMode mode, ModeCheckContext context) throws TypeModeError {
        checkValid();
        RBComponent converted = comp.convertToMode(mode, context);
        return new ValidatorComponent(converted, validator);
    }

    @Override
    public Mode getMode() {
        checkValid();
        return comp.getMode();
    }

    @Override
    public Compiled compile(final CompilationContext c) {
        checkValid();
        return comp.compile(c);
    }

}
