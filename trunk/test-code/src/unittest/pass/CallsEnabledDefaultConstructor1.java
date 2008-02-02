package unittest.pass;
import org.joe_e.testlib.ContainsNonFinal;

public class CallsEnabledDefaultConstructor1 {
	void foo() {
		new ContainsNonFinal();
	}
    
    final public boolean equals(Object other) {
        return false;
    }
}
