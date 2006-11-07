package test;

/*
 * TEST: an inner class extends an inner class.
 * Inherited restrictions pass,
 * Non-field variables in scope not checked.
 */

public class ExtendsHasInner extends HasInner {
	int foo2;
	
    {
        new Exception().printStackTrace();
        System.out.println("Hello from ExtendsHasInner's block " + this);
        foo3 = 5;
        new Inner();
    }
    
      
    ExtendsHasInner() {
        super();
        /* field and instance initializers goes here */
        System.out.println("Hello from ExtendsHasInner's constructor " + this);
    }
    
    int foo3;
    
	class ExtendsInner extends Inner {
        Object o = ExtendsHasInner.this;
		int barf;
	}

}
