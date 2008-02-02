package unittest.pass;

class DontPrintMe {
	
}

public class EnabledStringConversion2 {
	String f() {
		return "hello" + 32 + 4.5 + new Object() {
										public String toString() { 
											return "a";
										}
									};
	}
}
