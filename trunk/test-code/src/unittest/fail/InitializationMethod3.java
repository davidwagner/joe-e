package unittest.fail;

public class InitializationMethod3 {
	class Foo {
		class Bar {
			class Baz {
				void foo() {
					
				}
			}
			
			class Baz2 extends Baz {
				Baz2() {
					foo();
				}
					
				class Qux {
					class Quux {
						
					}
				}
			}
		}
	}
}
