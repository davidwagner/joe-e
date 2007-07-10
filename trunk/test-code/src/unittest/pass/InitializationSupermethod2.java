package unittest.pass;

class Quasar {
	static boolean floop(Object o) {
		return true;
	}
}

public class InitializationSupermethod2 extends Quasar {
	boolean b;
	{
		b = super.floop(null);
	}
}
