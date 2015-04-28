package tyRuBa.util;

class EmptySource extends ElementSource {

    public static EmptySource the = new EmptySource();

    @Override
    public int status() {
        return NO_MORE_ELEMENTS;
    }

    @Override
    public Object nextElement() {
        throw new Error("TheEmpty ElementSource has no elements");
    }

    /** More efficient append to forget about useless empty sources */
    @Override
    public ElementSource append(ElementSource other) {
        return other;
    }

    /** More efficient map for empty sources */
    @Override
    public ElementSource map(Action what) {
        return theEmpty;
    }

    @Override
    public void print(PrintingState p) {
        p.print("{*empty*}");
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ElementSource first() {
        return this;
    }

}