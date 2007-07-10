package unittest.fail;

class Super2 {
	final org.joe_e.Token t = null;
}

public class PowerlessInheritsNonPowerlessField1 extends Super2 
										implements org.joe_e.Powerless {
	final Exception e = null;
	final String s = null;
}
