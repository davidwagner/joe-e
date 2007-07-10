package unittest.fail;

import org.joe_e.testlib.ContainsNonImmutable;

class A extends ContainsNonImmutable {}

class B extends A {}

class C extends B {}

interface ImmutablePlus extends org.joe_e.Immutable {}

public class ImmutableInheritsNonImmutableField3 extends C
										implements ImmutablePlus {
	final Exception e = null;
	final String s = null;
}