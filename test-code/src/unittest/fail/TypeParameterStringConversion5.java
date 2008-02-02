package unittest.fail;

import org.joe_e.Token;
import java.io.Serializable;

class Enabler5<T extends Serializable> {
	T t;
	Enabler5(T t) {
		this.t = t;
	}
	
	T getT() {
		return t;
	}
}

public class TypeParameterStringConversion5 {
	Enabler5 e = new Enabler5<Serializable>(new Token());
	Enabler5<String> e2 = e;
	
	String s = (e2.getT()) + "";
}