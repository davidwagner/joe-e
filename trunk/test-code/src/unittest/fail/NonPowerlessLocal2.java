package unittest.fail;

import org.joe_e.*;
import org.joe_e.array.ImmutableArray;

class PowerlessException extends Exception implements Powerless {
	static final long serialVersionUID = 1;
}

public class NonPowerlessLocal2 implements Powerless {
    void method() {
	    final Token t = new Token();
		final ImmutableArray<Token> iat = ImmutableArray.array(t, t);
	    
		new Powerless() {
			class A implements Powerless {
				class B implements Powerless {
					public boolean equals(Object o) {
						new PowerlessException() {
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
