package unittest.pass;

import org.joe_e.*;
import org.joe_e.array.ImmutableArray;

interface PowerlessPlus5 extends Powerless {
}

interface HasChild {
	Object getChild();
}

public class InheritedLocalFromSameClass implements Powerless {
    void method() {
	    final Token t = new Token();
		final ImmutableArray<Token> iat = ImmutableArray.array(t, t);
	    
		class A {
			Object o;
			
			void init() {
				o = new HasChild() {
					class B implements HasChild {
						class C {
							public boolean equals(Object o) {
								new Object() {
									static final long serialVersionUID = 1;
									public String toString() {
										return (iat.get(0) == null) ? "a" : "b";
									}
								};
								return false;
							}
						}
						public Object getChild() {
							return new C();
						}
					}
					public Object getChild() { 
						return new B(); 
					}
				};
			}
		}
		
		class Foo implements HasChild {
			public Object getChild() {
				A a = new A();
				a.init();
				return a.o;
			}
		}
				
		new Foo().getChild();
	}
}
