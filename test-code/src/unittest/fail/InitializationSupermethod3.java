package unittest.fail;

public class InitializationSupermethod3 {
	class Foo {
		class Bar {
			class Baz {
				void foo() {
					
				}
			}
			
			class Baz2 extends Baz {
				Baz2() {
					super.foo();
				}
					
				class Qux {
					class Quux {
						
					}
				}
			}
		}
	}
}
