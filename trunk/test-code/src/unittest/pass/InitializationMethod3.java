package unittest.pass;

public class InitializationMethod3 {
	static class Foo {
		static class Bar {
			static class Baz {
				static void foo() {
					
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
