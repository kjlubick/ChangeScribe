package tyRuBa.engine.visitor;

import tyRuBa.engine.Frame;
import tyRuBa.engine.RBCompoundTerm;
import tyRuBa.engine.RBIgnoredVariable;
import tyRuBa.engine.RBPair;
import tyRuBa.engine.RBQuoted;
import tyRuBa.engine.RBTemplateVar;
import tyRuBa.engine.RBTerm;
import tyRuBa.engine.RBTuple;
import tyRuBa.engine.RBVariable;
import tyRuBa.modes.ConstructorType;

public class SubstantiateVisitor implements TermVisitor {

    Frame subst;

    Frame inst;

    public SubstantiateVisitor(Frame subst, Frame inst) {
        this.subst = subst;
        this.inst = inst;
    }

    @Override
    public Object visit(RBCompoundTerm compoundTerm) {
        ConstructorType typeConst = compoundTerm.getConstructorType();
        return typeConst.apply(
                (RBTerm) compoundTerm.getArg().accept(this));
        // PredicateIdentifier pred = compoundTerm.getPredId();
        // return RBCompoundTerm.makeForVisitor(pred,
        // (RBTerm)compoundTerm.getArgsForVisitor().accept(this));
    }

    @Override
    public Object visit(RBTuple tuple) {
        RBTerm[] subterms = new RBTerm[tuple.getNumSubterms()];
        for (int i = 0; i < subterms.length; i++) {
            subterms[i] = (RBTerm) tuple.getSubterm(i).accept(this);
        }
        return RBTuple.make(subterms);
    }

    @Override
    public Object visit(RBPair pair) {
        RBPair head = new RBPair((RBTerm) pair.getCar().accept(this));

        RBPair next;
        RBPair prev = head;

        RBTerm cdr = pair.getCdr();

        while (cdr instanceof RBPair) {
            pair = (RBPair) cdr;
            next = new RBPair((RBTerm) pair.getCar().accept(this));
            prev.setCdr(next);
            prev = next;
            cdr = pair.getCdr();
        }

        prev.setCdr((RBTerm) cdr.accept(this));

        return head;
    }

    @Override
    public Object visit(RBQuoted quoted) {
        return new RBQuoted(
                (RBTerm) quoted.getQuotedParts().accept(this));
    }

    @Override
    public Object visit(RBVariable var) {
        RBTerm val = subst.get(var);
        if (val == null) {
            return var.accept(new InstantiateVisitor(inst));
        } else {
            return val.accept(this);
        }
    }

    @Override
    public Object visit(RBIgnoredVariable ignoredVar) {
        return ignoredVar;
    }

    @Override
    public Object visit(RBTemplateVar templVar) {
        // Instantiation only happens at runtime. TemplateVar should not
        // exsit any more at runtime so...
        throw new Error("Unsupported operation");
    }

}
