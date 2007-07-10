package unittest.fail;

import org.joe_e.Immutable;

// Check on the type of qualifiers to super() expressions in
// an immutable class is not enough.  Ouch!
class Outer3 {
	class Inner {
	}
}

class ExtendsOuter3 extends Outer3  implements org.joe_e.Powerless {
	class ExtendsInner extends Inner {
		ExtendsInner() {
			new Outer3().super();
		}
	}
}

class ExtendsExtendsOuter3 extends ExtendsOuter3 {
	class ExtendsExtendsInner extends ExtendsInner implements Immutable {
	}
}