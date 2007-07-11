package unittest.broken;

import org.joe_e.Struct;
import org.joe_e.Powerless;

public class AnonymousClassBug implements Powerless {
	public Object build(final AnonymousClassBug next) {
        return new Object() {
            public void
            run(final Struct a) {
            	new Runnable() {
                    public void
                    run() { a.equals(a); }
                };
            }
        };
    }
	
	void f() {
		class A {
			void party() {
				final int[] foo = {0};
				class B {
					void up() {
						foo[0]++;
					}
				}
			}
		}
		
		new A();
	}
}
