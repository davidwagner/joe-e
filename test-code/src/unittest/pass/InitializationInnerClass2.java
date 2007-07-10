package unittest.pass;

public class InitializationInnerClass2 {
	static class Foo {
		static {
			new Foo().new Bar();
		}
		
		class Bar {
		}
	}
}
