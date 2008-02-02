package unittest.fail;

class Enabler6<T> {
	T t;
	Enabler6(T t) {
		this.t = t;
	}
	
	T getT() {
		return t;
	}
}

public class TypeParameterStringConversion6 {
	Enabler6 e = new Enabler6<Object>(new Object());
	Enabler6<String> e2 = e;
	
	String s = "a" + 5 + 12 + (true? e2 : e2).t;
}