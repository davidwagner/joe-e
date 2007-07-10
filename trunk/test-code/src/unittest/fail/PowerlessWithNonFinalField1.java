package unittest.fail;

class PowerlessType implements org.joe_e.Powerless {

}

public class PowerlessWithNonFinalField1 extends PowerlessType {
	final Exception e = null;
	String s = null;
}
