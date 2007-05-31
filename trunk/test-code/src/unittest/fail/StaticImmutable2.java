package unittest.fail;

public class StaticImmutable2 {
    static class Inner {
	    static final Inner i = new Inner();
	}
}
