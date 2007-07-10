package unittest.fail;

public class InitializationInnerClass2 {
	static class Foo {
		{
			new Bar();
		}
		
		class Bar {
		}
	}
}
