package test;
import org.joe_e.Powerless;

public class StrawMan implements Powerless {
	static {
		class Gradius {}
		
	}
	
	private final String contents;
	final int foo = local();
    static int foo2 = staticLocal();
    
	public StrawMan(String contents) {
        class Foofy {
            int foo;
            int bar;
            
            void fart() {
                System.out.println(StrawMan.this);
            }
        };
        
        new Foofy().fart();
        this.contents = contents;
	}
   
    static int staticLocal() {
        // do bad stuff
        return 6;
    }
    
    int local() {
        System.out.println(this);
        return 5;
    }
    
	public String toString() {
		return contents;
	}
	
	public boolean equals(Object o) {
		if (o instanceof StrawMan) {
			StrawMan other = (StrawMan) o;
			return other.contents.equals(this.contents) &&
				   other.foo == this.foo;
		} else {
			return false;
		}
	}
	
    public boolean equals2(Object o) {
		return super.equals(o);
	}
	
	public int hashCode() {
		return 57;
	}
	
	public Object invoke(Object o, java.lang.reflect.Method m, Object[] oa) {
		return new Object();
	}
}
