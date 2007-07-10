package unittest.fail;

import org.joe_e.testlib.MembersDisabled;

public class AccessesDisabledField1 {
	Object o = ((MembersDisabled) null).i;
}
