package unittest.fail;

public class ImmutableInNonImmutable1 {
	class Inner implements org.joe_e.Immutable {
		final Exception e = null;
		final String s = null;
	}
}