package unittest.fail;

import org.joe_e.testlib.HasHonorary;

public class UnimplementedHonorary2 implements org.joe_e.Immutable {
	void foo() {
		new HasHonorary() {
			static final long serialVersionUID = 1;
		};
	}
}
