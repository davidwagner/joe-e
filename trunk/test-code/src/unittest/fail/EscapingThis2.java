package unittest.fail;

import org.joe_e.array.ConstArray;

public class EscapingThis2 {
	Object o;
	{
		o = ConstArray.array(EscapingThis2.this);
	}
}
