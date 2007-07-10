package unittest.pass;

import org.joe_e.Powerless;

class Weird extends Throwable implements Powerless {
	static final long serialVersionUID = 1;
}

public class CatchingNonExceptionThrowable {
	{
		try {
			if (new String() == null) {
				throw new Weird();
			}
		} catch (Weird w) {
			
		}
	}
}
