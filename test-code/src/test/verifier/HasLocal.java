package test.verifier;
import org.joe_e.Powerless;
import org.joe_e.Immutable;

public class HasLocal implements Powerless{

	final int foo = 5;
	
    {
        System.out.println("Hello from HasInner's block");
    }
    
    HasLocal() {
        System.out.println("Hello from HasInner's constructor");
        //new Inner();
        new ExtendsInner();
    }
    
    void doStuff() {
    	final Object o = new String();
    	class Local implements Powerless {
    		class InnerOfLocal implements Powerless {
    			final int bar = (o == null)? foo : 3;
    		}
        /*
		public boolean equals(Object obj) {
			if (obj instanceof Gullible) {
			    ((Gullible) obj).give(HasInner.this);
            }
            return false;
		}
        */
    	}
    }
    
	static class StaticInner implements Immutable {
		
	}
	
	class Inner {
	}
	
}
