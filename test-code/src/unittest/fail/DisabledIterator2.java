package unittest.fail;

import org.joe_e.testlib.DisabledIterator;

public class DisabledIterator2 {
    public void foo() {
    	DisabledIterator<String> i = null;
    	
        for (String s : i) {
        	s.length();
        }
    }
}
