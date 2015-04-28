/*
 * Created on Jul 3, 2003
 */
package tyRuBa.engine.visitor;

import tyRuBa.engine.Frame;
import tyRuBa.engine.RBExpression;
import tyRuBa.engine.RBTemplateVar;
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBUniqueQuantifier;
import tyRuBa.engine.RBVariable;

public class SubstituteVisitor extends SubstituteOrInstantiateVisitor {

	public SubstituteVisitor(Frame frame) {
		super(frame);
	}

	@Override
    public Object visit(RBUniqueQuantifier unique) {
		Frame f = getFrame();
		for (int i = 0; i < unique.getNumVars(); i++) {
			f.remove(unique.getVarAt(i));
		}
		RBExpression exp = (RBExpression) unique.getExp().accept(this);
		return new RBUniqueQuantifier(unique.getQuantifiedVars(), exp);
	}

	@Override
    public Object visit(RBVariable var) {
		RBTerm val = getFrame().get(var);
		if (val == null) {
			return var;
		} else {
			return val.accept(this);
		}
	}

	@Override
    public Object visit(RBTemplateVar var) {
		// Same implementation as for regular variables
		RBTerm val = getFrame().get(var);
		if (val == null) {
			return var;
		} else {
			return val.accept(this);
		}
	}
    
}
