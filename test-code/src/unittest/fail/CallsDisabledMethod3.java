package unittest.fail;

import org.joe_e.testlib.MembersDisabled;

public class CallsDisabledMethod3 {
	MembersDisabled md = null;
	{
		md.foo();
	}
}
