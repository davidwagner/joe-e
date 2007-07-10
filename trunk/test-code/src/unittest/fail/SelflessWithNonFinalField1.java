package unittest.fail;

class SelflessType2 implements org.joe_e.Selfless {
	public boolean equals(Object o) {
		return false;
	}
	
	public int hashCode() {
		return 0;
	}
}

public class SelflessWithNonFinalField1 extends SelflessType2 {
	final Exception e = null;
	String s = null;
}
