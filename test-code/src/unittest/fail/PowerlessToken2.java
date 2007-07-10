package unittest.fail;

import org.joe_e.*;

class Bob2 extends Token {
	static final long serialVersionUID = 1;
}

interface PowerlessPlus extends Powerless {
	
}

public class PowerlessToken2 extends Bob2 implements PowerlessPlus {
	static final long serialVersionUID = 1;
}
