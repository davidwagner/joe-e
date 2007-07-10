package unittest.fail;

class A4 {
	transient final String s = "";
}

class B4 extends A4 {}

class C4 extends B4 {}

interface PowerlessPlus4 extends org.joe_e.Powerless {}

public class PowerlessInheritsTransientField extends C4
										implements PowerlessPlus4 {
	final Exception e = null;
	final String s = null;
}