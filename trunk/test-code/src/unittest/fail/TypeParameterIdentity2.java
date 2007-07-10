package unittest.fail;

import org.joe_e.Equatable;

class Enabler<T> {
	T t;
	Enabler(T t) {
		this.t = t;
	}
	
	T getT() {
		return t;
	}
}

public class TypeParameterIdentity2 {
	Enabler e = new Enabler<Object>(new Object());
	Enabler<Equatable> e2 = e;
	Enabler<Equatable> e3 = e2;
	
	boolean b = (true? e2 : e2).t == e3.getT();
}