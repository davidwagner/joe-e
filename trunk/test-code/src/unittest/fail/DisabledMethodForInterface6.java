package unittest.fail;

import org.joe_e.testlib.DisabledMethodTakesT;

interface HasTTakerOfString {
	void takesT(String o);
}

// implements generic interface by means of a bridge method
// for a disabled method
public class DisabledMethodForInterface6 extends DisabledMethodTakesT<String>
										implements HasTTakerOfString {
	
}
