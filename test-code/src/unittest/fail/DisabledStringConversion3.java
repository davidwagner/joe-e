package unittest.fail;

class DontPrintMe3 {
	
}

public class DisabledStringConversion3 {
	String f() {
		assert 5 == 6 : new DontPrintMe3();
		return null;
	}
}
