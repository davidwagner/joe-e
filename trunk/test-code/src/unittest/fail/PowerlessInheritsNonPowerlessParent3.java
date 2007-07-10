package unittest.fail;

import org.joe_e.Powerless;

// Check on the type of qualifiers to super() expressions in
// an immutable class is not enough.  Ouch!
class Outer6 {
	class Inner {
	}
}

class ExtendsOuter6 extends Outer6  implements Powerless {
	class ExtendsInner extends Inner {
		ExtendsInner() {
			new Outer6().super();
		}
	}
}

class ExtendsExtendsOuter6 extends ExtendsOuter6 {
	class ExtendsExtendsInner extends ExtendsInner implements Powerless {
	}
}