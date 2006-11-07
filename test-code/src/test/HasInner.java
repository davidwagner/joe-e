package test;
import org.joe_e.Powerless;
import org.joe_e.Immutable;

public class HasInner {

	int foo;
	
    {
        System.out.println("Hello from HasInner's block");
    }
    
    HasInner() {
        System.out.println("Hello from HasInner's constructor");
        new Inner();
    }
    
	class Inner {
		final int bar = foo;
		
        /*
		public boolean equals(Object obj) {
			if (obj instanceof Gullible) {
			    ((Gullible) obj).give(HasInner.this);
            }
            return false;
		}
        */
	}
	
	static class StaticInner implements Immutable {
		
	}
}
