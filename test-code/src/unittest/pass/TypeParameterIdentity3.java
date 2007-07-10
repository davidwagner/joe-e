package unittest.pass;

import org.joe_e.Equatable;
import org.joe_e.Powerless;

class Enabler2<T extends Equatable> {
	T t;
	Enabler2(T t) {
		this.t = t;
	}
	
	T getT() {
		return t;
	}
}

public class TypeParameterIdentity3 {
	static class Identifiable implements Equatable, Powerless {
	};
	
	boolean doStuff() {
		Enabler2 e = new Enabler2<Equatable>(new Equatable() {});
		Enabler2<Identifiable> e2 = e;
		Enabler2<Identifiable> e3 = e2;
	
		return e2.t == ((e3.getT()));
	}
}