package lsd.rule;

@SuppressWarnings("serial")
public class LSDInvalidTypeException extends Exception {
    @Override
    public String toString() {
        return "Type mismatch or type is not one of the defined types.";
    }
}
