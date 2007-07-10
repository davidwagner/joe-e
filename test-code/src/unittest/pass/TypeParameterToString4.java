package unittest.pass;

class Enabler4<T> {
	T t;
	Enabler4(T t) {
		this.t = t;
	}
	
	T getT() {
		return t;
	}
}

public class TypeParameterToString4 {
	Enabler4 e = new Enabler4<Object>(new Object());
	Enabler4<String> e2 = e;
	
	String s = (true? e2.getT() : e2.getT()) + "";
}