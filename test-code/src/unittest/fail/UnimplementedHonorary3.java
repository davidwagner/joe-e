package unittest.fail;

import org.joe_e.testlib.HasHonorary2;

public class UnimplementedHonorary3 {
	void foo() {
		new HasHonorary2(new Object()) {
			static final long serialVersionUID = 1;
		};
	}
}
