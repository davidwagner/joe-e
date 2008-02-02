package unittest.pass;
import org.joe_e.testlib.ContainsNonFinal;

public class CallsEnabledDefaultConstructor2 {
	void foo() {
		new ContainsNonFinal() {
		    static final long serialVersionUID = 1;
        };
	}
    
    final public boolean equals(Object other) {
        return false;
    }
}
