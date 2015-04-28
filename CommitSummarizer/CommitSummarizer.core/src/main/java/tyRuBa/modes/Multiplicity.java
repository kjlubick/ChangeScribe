/*
 * Created on Apr 17, 2003
 */
package tyRuBa.modes;

/**
 * A Multiplicity object represents a kind of abstract number.
 * There are three Multiplicities:
 *
 *   - zero
 *   - one
 *   - many
 */
public abstract class Multiplicity implements Comparable {
	
	public static Multiplicity fromInt(int i) {
		if (i < 0) {
			throw new Error("Only works for positive integers");
		} else if (i==0) {
			return zero;
		} else if (i==1) {
			return one;
		} else { // (i>1)
			return many;
		}
	}
	
	public static final Multiplicity zero = new Multiplicity(0) {
		@Override
        public String toString() {
			return "Mult(0)";
		}
		@Override
        public Multiplicity multiply(Multiplicity other) {
			return this;
		}
		@Override
        public Multiplicity add(Multiplicity other) {
			return other;
		}
	};

	public static final Multiplicity one = new Multiplicity(1) {
		@Override
        public String toString() {
			return "Mult(1)";
		}
		@Override
        public Multiplicity multiply(Multiplicity other) {
			return other;
		}
		@Override
        public Multiplicity add(Multiplicity other) {
			if (other.equals(zero)) {
				return this;
			} else if (other.equals(infinite)) {
				return other;
			} else {
				return many;
			}
		}
	};

	public static final Multiplicity many = new Multiplicity(2) {
		@Override
        public String toString() {
			return "Mult(>1)";
		}
		@Override
        public Multiplicity multiply(Multiplicity other) {
			if (other.equals(zero)) {
				return other;
			} else if (other.equals(infinite)) {
				return other;
			} else {
				return this;
			}
		}
		@Override
        public Multiplicity add(Multiplicity other) {
			if (other.equals(infinite)) {
				return other;
			} else {
				return this;
			}
		}
	};

	public static final Multiplicity infinite = new Multiplicity(999) {
		@Override
        public String toString() {
			return "Mult(INFINITE)";
		}
		@Override
        public Multiplicity multiply(Multiplicity other) {
			if (other.equals(zero)) {
				return other;
			} else {
				return this;
			}
		}
		@Override
        public Multiplicity add(Multiplicity other) {
			return this;
		}
	};

	protected Multiplicity(int compareInt) {
		this.compareInt = compareInt;
	}

	/** Multiplicities are abstract numbers, so they can be multiplied 
	 * with one another */
	public abstract Multiplicity multiply(Multiplicity other);

	/** Multiplicities are abstract numbers, so they can be added 
	 * to one another */
	public abstract Multiplicity add(Multiplicity other);
	
	// Compare function assumes that there is a total
	// order and objects can be mapped onto integers to
	// reflect that order.	
	int compareInt;
	
		
	@Override
    public int compareTo(Object o) {
		if (this.compareInt < ((Multiplicity) o).compareInt) {
			return -1;
		} else if (this.compareInt>((Multiplicity) o).compareInt) {
			return 1;
		} else {
			return 0; 
		}
	}
	
	@Override
    public boolean equals(Object arg0) {
		return this.compareTo(arg0) == 0;
	}
	
	@Override
    public int hashCode() {
		return (compareInt+13)*113;
	}

	public Multiplicity min(Multiplicity other) {
		if (compareTo(other) == 1) {
			return other;
		} else {
			return this;
		}
	}

	public Multiplicity max(Multiplicity other) {
		if (compareTo(other) == -1) {
			return other;
		} else {
			return this;
		}
	}
}
