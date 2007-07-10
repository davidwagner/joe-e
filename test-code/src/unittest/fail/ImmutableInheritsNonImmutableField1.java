package unittest.fail;

class Super {
	final java.io.File f = null;
}

public class ImmutableInheritsNonImmutableField1 extends Super 
										implements org.joe_e.Immutable {
	final Exception e = null;
	final String s = null;
}
