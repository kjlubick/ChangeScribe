package tyRuBa.engine;

/**
 * Form key is an adapter to provide the right equals and hashCode to store something in a hashTable key such that it matches when it has the same form
 */
class FormKey {

    RBTerm theKey;

    FormKey(RBTerm t) {
        theKey = t;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof FormKey) {
            return theKey.sameForm(((FormKey) other).theKey);
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        return theKey.formHashCode();
    }

}
