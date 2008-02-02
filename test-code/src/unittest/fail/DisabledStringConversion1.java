package unittest.fail;

class DontPrintMe {
	
}

public class DisabledStringConversion1 {
	String f() {
		return "hello" + 32 + 4.5 + new DontPrintMe();
	}
}
