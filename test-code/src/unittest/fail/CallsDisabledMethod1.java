package unittest.fail;

import org.joe_e.testlib.MembersDisabled;

public class CallsDisabledMethod1 {
	{
		((MembersDisabled) null).foo();
	}
}
