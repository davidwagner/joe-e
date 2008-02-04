package unittest.fail;

import org.joe_e.Immutable;

public class ImmutableWithNonImmutableField4<E extends Iterable & Immutable>
										implements Immutable {
	final E ee = null;
	final Exception e = null;
	final String s = null;
}

