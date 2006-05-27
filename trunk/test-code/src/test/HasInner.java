package test;
import org.joe_e.Powerless;
import org.joe_e.Immutable;

public class HasInner {

	int foo;
	
	class Inner {
		final int bar = foo;
		
		void qux() {
			foo = 5;
		}
	}
	
	static class StaticInner implements Immutable {
		
	}
}
