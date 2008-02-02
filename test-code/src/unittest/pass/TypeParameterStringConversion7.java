package unittest.pass;

class Enabler7<T> {
	T t;
	Enabler7(T t) {
		this.t = t;
	}
	
	T getT() {
		return t;
	}
}

public class TypeParameterStringConversion7 {
	Enabler7 e = new Enabler7<Object>(new Object());
	Enabler7<String> e2 = e;
	
	{
		assert 5 < 3 : (String) e2.getT();
	}
}