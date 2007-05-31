package test.verifier;

import org.joe_e.Selfless;

import test.StrawMan;

public class SelflessTest2<E> extends StrawMan implements Selfless, java.lang.reflect.InvocationHandler {
	SelflessTest2() {
		super("fred");
	}
	
	public boolean equals(Object o) {
		return super.equals(o);
	}
	
	public boolean useE(E a) {
		return true;
	}
	
	//public int hashCode() {
	//	return super.hashCode();
	//}
}
