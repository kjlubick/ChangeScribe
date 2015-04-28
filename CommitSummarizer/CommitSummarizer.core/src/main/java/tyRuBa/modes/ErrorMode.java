package tyRuBa.modes;

public class ErrorMode extends Mode {
	
	String msg;
		
	public ErrorMode(String msg) {
		super(Multiplicity.zero, Multiplicity.infinite);
		this.msg = msg;
	}
			
	@Override
    public String toString() {
		return "ERROR: " + msg;
	}
	
	@Override
    public boolean equals(Object other) {
		if (other instanceof ErrorMode) {
			return msg.equals(((ErrorMode)other).msg);
		} else {
			return false;
		}
	}
	
	@Override
    public int hashCode() {
		return 122 + msg.hashCode();
	}

	@Override
    public Mode add(Mode other) {
		return this;
	}

}
