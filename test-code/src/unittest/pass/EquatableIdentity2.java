package unittest.pass;

import org.joe_e.*;

public class EquatableIdentity2 {
	enum Food implements Powerless, Equatable {
		PIZZA, PASTA, ANTIPASTO("the opposite of pasto?");
		
		final String description;
		Food() {
			this.description = "none";
		}
		
		Food(String description) {
			this.description = description;
		}
	}
	
	Boolean bool = Food.PIZZA != Food.ANTIPASTO;
}
