package unittest.fail;

class DontPrintMe2 {
	
}

public class DisabledToString2 {
	String f() {
		String s = "hello" + 32 + 4.5;
		s += new DontPrintMe2();
		return s;
	}
}
