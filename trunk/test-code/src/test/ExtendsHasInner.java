package test;

/*
 * TEST: an inner class extends an inner class.
 * Inherited restrictions pass,
 * Non-field variables in scope not checked.
 */

public class ExtendsHasInner extends HasInner {
	int foo2;
	
	class ExtendsInner extends Inner {
		int barf;
	}

}
