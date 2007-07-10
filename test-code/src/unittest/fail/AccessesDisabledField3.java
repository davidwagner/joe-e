package unittest.fail;

import org.joe_e.testlib.MembersDisabled;

public class AccessesDisabledField3 {
	MembersDisabled md = null;
	Object o = md.i;
}
