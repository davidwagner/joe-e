package test;
import org.joe_e.Powerless;

public class StrawMan implements Powerless {
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
}
