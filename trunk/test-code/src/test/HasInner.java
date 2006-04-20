package test;
import org.joe_e.Incapable;
import org.joe_e.DeepFrozen;

public class HasInner {

	int foo;
	
	class Inner {
		final int bar = foo;
		
		void qux() {
			foo = 5;
		}
	}
	
	static class StaticInner implements DeepFrozen {
		
	}
}
