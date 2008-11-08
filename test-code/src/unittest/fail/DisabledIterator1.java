package unittest.fail;

public class DisabledIterator1 {
    public void foo(Iterable<String> is) {
        for (String s : is) {
        	s.length();
        }
    }
}
