package unittest.fail;

import org.joe_e.testlib.DisabledIterable;

public class DisabledIterable2 {
    public void foo() {
    	DisabledIterable<String> i = null;
    	
        for (String s : i) {
        	s.length();
        }
    }
}
