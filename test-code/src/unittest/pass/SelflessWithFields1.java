package unittest.pass;

class SelflessType2 implements org.joe_e.Selfless {
	public boolean equals(Object o) {
		return false;
	}
	
	public int hashCode() {
		return 0;
	}
}

public class SelflessWithFields1 extends SelflessType2 {
	final Exception e = null;
	final String s = null;
	final int[] array = {1, 2, 3, 4};
	final org.joe_e.Token t = null;
	final java.io.File f = null;
}
