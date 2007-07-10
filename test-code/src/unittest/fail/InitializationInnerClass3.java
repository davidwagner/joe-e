package unittest.fail;

public class InitializationInnerClass3 {
	class Foo {
		class Bar {
			class Baz {
				Object o;
				
				Baz() {
					o = new Qux();
				}
					
				class Qux {
					class Quux {
						
					}
				}
			}
		}
	}
}
