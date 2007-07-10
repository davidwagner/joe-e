package unittest.fail;

interface ImmutablePlus2 extends org.joe_e.Immutable {}

public class ImmutableWithNonFinalField1 implements ImmutablePlus2 {
	final Exception e = null;
	String s = null;
}
