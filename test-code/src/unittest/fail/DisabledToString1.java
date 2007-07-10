package unittest.fail;

class DontPrintMe {
	
}

public class DisabledToString1 {
	String f() {
		return "hello" + 32 + 4.5 + new DontPrintMe();
	}
}
