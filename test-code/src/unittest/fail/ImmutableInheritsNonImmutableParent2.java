package unittest.fail;

interface ImmutablePlus4 extends org.joe_e.Immutable {
	
}

class Outer2 {
	class Inner {
		
	}
}


class ExtendsOuter extends Outer2 implements org.joe_e.Powerless {
	class ExtendsInner extends Inner implements ImmutablePlus4 {
		ExtendsInner() {}
		
		ExtendsInner(Outer2 o) {
			o.super();
		}
	}
}




