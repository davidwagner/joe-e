package unittest.pass;

public class NonImmutableLocal1 implements org.joe_e.Powerless {
	static void foop() {
		final int[] garbage = new int[4];
		class Gorbachev {
			public String toString() {
				return "" + garbage[0];
			}
		}
	}
}
