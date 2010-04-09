package unittest.pass;

import org.joe_e.*;
import org.joe_e.array.ImmutableArray;

class ExtendsToken2 extends Token {
	static final long serialVersionUID = 1;
}

public class ImmutableLocal4 implements Powerless {
    void method(final ImmutableArray<Token> iat) {
		new Powerless() {
			class A implements Powerless {
				class B implements Immutable {
					public boolean equals(Object o) {
						new ExtendsToken2() {
							static final long serialVersionUID = 1;
							public String toString() {
								return (iat.get(0) == null) ? "a" : "b";
							}
						};
						return false;
					}
				}
			}
		};
	}
}
