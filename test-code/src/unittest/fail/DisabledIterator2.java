package unittest.fail;

import org.joe_e.testlib.DisabledIterable;

public class DisabledIterator2 {
    public void foo() {
    	DisabledIterable<String> i = new DisabledIterable<String>();
    	
        for (String s : i) {
        	s.length();
        }
    }
}
