package unittest.pass;

class Enabler3<T> {
	T t;
	Enabler3(T t) {
		this.t = t;
	}
	
	T getT() {
		return t;
	}
}

public class TypeParameterStringConversion3 {
	Enabler3 e = new Enabler3<Object>(new Object());
	Enabler3<String> e2 = e;
	
	String s = (String) e2.t + "";
}