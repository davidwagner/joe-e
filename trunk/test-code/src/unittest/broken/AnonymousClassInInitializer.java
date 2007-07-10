package unittest.broken;

import org.joe_e.Powerless;

public class AnonymousClassInInitializer implements Powerless {
	static {
		// the following causes problems for resolution of locations
		// of local variables if not prohibited
		final int fives[] = {5, 5};
		
		new Object() {
			public String toString() {
				return fives[0] + "!";
			}
		};
	}
}
