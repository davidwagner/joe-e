package unittest.fail;

public class StaticNonPowerless4 {
    static class Inner implements org.joe_e.Immutable {
	    static final Inner i = new Inner();
	}
}
