package unittest.pass;

public class StaticFields2 {
	static class Inner {
		static final int bar = 34;
		static final String s = "powerless";
		static final Double d = 3.4;
		static final Short shorty = -78;
		static final Throwable t = new Throwable();
	}
}
