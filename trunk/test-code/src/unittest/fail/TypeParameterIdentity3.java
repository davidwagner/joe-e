package unittest.fail;

import org.joe_e.Equatable;

class Enabler2<T> {
	T t;
	Enabler2(T t) {
		this.t = t;
	}
	
	T getT() {
		return t;
	}
}

public class TypeParameterIdentity3 {
	Enabler2 e = new Enabler2<Object>(new Object());
	Enabler2<Equatable> e2 = e;
	Enabler2<Equatable> e3 = e2;
	
	boolean b = e2.t == ((e3.getT()));
}