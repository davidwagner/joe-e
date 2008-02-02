package unittest.fail;

import org.joe_e.*;

public enum EnumWithNonImmutableField1 implements Powerless, Equatable {
	a, b, c;
	
	final String[] arr = new String[]{"hi"};
	
	String[] getNotImmutable() {
		return arr;
	}
}
