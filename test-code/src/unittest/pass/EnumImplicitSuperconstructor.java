package unittest.pass;

import org.joe_e.*;

public class EnumImplicitSuperconstructor {
	enum Tristate implements Powerless, Equatable {
	    YES, NO, MAYBE("I just don't know!");
		
	    final String comment;
	    
	    Tristate() {
	    	comment = null;
	    }
	    
	    Tristate(String comment) {
	    	this.comment = comment;
	    }
	}
}
