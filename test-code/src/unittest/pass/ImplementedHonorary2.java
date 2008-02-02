package unittest.pass;

import org.joe_e.Powerless;
import org.joe_e.Equatable;

public enum ImplementedHonorary2 implements Powerless, Equatable {
	a, 
	b, 
	c {
		final int five = 5; 
		public int getFive() {
			return five;
		}
	};
	
	public int getFive() {
		return 7;
	}
}
