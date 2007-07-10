package unittest.fail;

import org.joe_e.testlib.ContainsNonImmutable;

public class ImmutableInheritsNonImmutableField2 extends ContainsNonImmutable
										implements org.joe_e.Immutable {
	final Exception e = null;
	final String s = null;
}

