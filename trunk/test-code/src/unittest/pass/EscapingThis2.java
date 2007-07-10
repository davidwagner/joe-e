package unittest.pass;

import org.joe_e.array.ConstArray;

public class EscapingThis2 {
	Object o;
	
	class Inner {
		{
			o = ConstArray.array(EscapingThis2.this);
		}
	}
}
