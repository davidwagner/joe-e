package unittest.pass;

public class CallsEnabledConstructor3 {
    enum Foo {
        a, b, c {int i = 5; };
    }
    
	void foo() {
		new Exception("comment") {
		    static final long serialVersionUID = 1;
        };
	}
    
    final public boolean equals(Object other) {
        return false;
    }
}
