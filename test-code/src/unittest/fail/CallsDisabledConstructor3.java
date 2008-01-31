package unittest.fail;

public class CallsDisabledConstructor3 {
	void foo () {
		new java.io.File("foo") {
		    static final long serialVersionUID = 1;
        };
	}
}
