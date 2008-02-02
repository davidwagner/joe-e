package unittest.fail;

public class DisabledIterator1 {
    public void foo() {
    	Iterable<String> i = null;
    	
        for (String s : i) {
        	s.length();
        }
    }
}
