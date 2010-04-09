package unittest.fail;

import org.joe_e.*;
import org.joe_e.array.ImmutableArray;

class PowerlessException2 extends Exception implements Powerless {
	static final long serialVersionUID = 1;
}

public class NonPowerlessLocal4 implements Powerless {
    void method(final ImmutableArray<Token> iat) {
		new Powerless() {
			class A implements Powerless {
				class B implements Powerless {
					public boolean equals(Object o) {
						new PowerlessException2() {
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
