package unittest.fail;

import org.joe_e.*;

public enum EnumWithNonFinalField1 implements Powerless, Equatable {
	a, b, c;
	
	int notFinal = -34; // for now...
	
	int getNotFinal(int newVal) {
		int temp = notFinal;
		notFinal = newVal;
		return temp;
	}
}
