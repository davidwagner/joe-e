package unittest.pass;

import org.joe_e.Equatable;
import org.joe_e.Powerless;

enum Farkingly implements Powerless, Equatable { HELLO, GOODBYE };

class Enabler<T extends Equatable> {
	T t;
	Enabler(T t) {
		this.t = t;
	}
	
	T getT() {
		return t;
	}
}

public class TypeParameterIdentity2 {
	boolean foop() {
		Enabler e = new Enabler<Equatable>(new Equatable() {});
		Enabler<Farkingly> e2 = e;
	
		return e2.t == Farkingly.GOODBYE;
	}
}