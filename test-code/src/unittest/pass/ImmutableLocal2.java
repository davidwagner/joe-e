package unittest.pass;

import org.joe_e.*;
import org.joe_e.array.ImmutableArray;

class ExtendsToken extends Token {
	static final long serialVersionUID = 1;
}

public class ImmutableLocal2 implements Powerless {
    void method() {
	    final Token t = new Token();
		final ImmutableArray<Token> iat = ImmutableArray.array(t, t);
	    
		new Powerless() {
			class A implements Powerless {
				class B implements Immutable {
					public boolean equals(Object o) {
						new ExtendsToken() {
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
