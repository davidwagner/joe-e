package unittest.fail;

interface PowerlessPlus5 extends org.joe_e.Powerless {
	
}

class Outer5 implements org.joe_e.Immutable {
	class Inner {
		
	}
}


class ExtendsOuter5 extends Outer5 implements org.joe_e.Powerless {
	class ExtendsInner extends Inner implements PowerlessPlus5 {
		ExtendsInner() {}
		
		ExtendsInner(Outer5 o) {
			o.super();
		}
	}
}




