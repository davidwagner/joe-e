package unittest.fail;

import org.joe_e.testlib.HasHonorary;

public class UnimplementedHonorary2 {
	void foo() {
		new HasHonorary() {
			static final long serialVersionUID = 1;
		};
	}
}
