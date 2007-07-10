package unittest.fail;

class A3 {
	transient final String s = "";
}

class B3 extends A3 {}

class C3 extends B3 {}

interface ImmutablePlus3 extends org.joe_e.Immutable {}

public class ImmutableInheritsTransientField extends C3
										implements ImmutablePlus3 {
	final Exception e = null;
	final String s = null;
}