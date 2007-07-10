package unittest.pass;

interface Printable5 {
	String toString();
}

class Foo5 implements Printable5 {
	public String toString() {
		return "a";
	}
}

class Enabler5<T extends Printable5> {
	T t;
	Enabler5(T t) {
		this.t = t;
	}
	
	T getT() {
		return t;
	}
}

public class TypeParameterToString5 {
	Enabler5<Foo5> e = new Enabler5<Foo5>(new Foo5());
	Enabler5<Foo5> e2 = e;
	
	String s = e2.getT() + "";
}