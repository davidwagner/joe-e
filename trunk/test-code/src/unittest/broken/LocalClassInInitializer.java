package unittest.broken;

import org.joe_e.Powerless;

public class LocalClassInInitializer implements Powerless {
	{
		// the following exposes a bug in eclipse if not prevented
		final int fives[] = {5, 5};
		
		class Local implements Powerless {
			public String toString() {
				return fives[0] + "!";
			}
		};
	}
}
