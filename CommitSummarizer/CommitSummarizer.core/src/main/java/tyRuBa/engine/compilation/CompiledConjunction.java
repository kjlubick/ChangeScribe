package tyRuBa.engine.compilation;

import tyRuBa.engine.RBContext;
import tyRuBa.util.ElementSource;

public class CompiledConjunction extends Compiled {

    private Compiled right;

    private Compiled left;

    public CompiledConjunction(Compiled left, Compiled right) {
        super(left.getMode().multiply(right.getMode()));
        this.left = left;
        this.right = right;
    }

    @Override
    public ElementSource run(ElementSource inputs, RBContext context) {
        return right.run(left.run(inputs, context), context);
    }

    @Override
    public ElementSource runNonDet(Object input, RBContext context) {
        return right.run(left.runNonDet(input, context), context);
    }

    @Override
    public String toString() {
        return "(" + right + "==>" + left + ")";
    }

}
