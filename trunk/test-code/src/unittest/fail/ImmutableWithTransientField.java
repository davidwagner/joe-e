package unittest.fail;

class Fooey implements org.joe_e.Immutable {
	
}

public class ImmutableWithTransientField extends Fooey {
	final Exception e = null;
	final transient String s = null;
}

