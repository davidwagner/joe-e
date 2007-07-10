package test.verifier;

public class InnerTest implements org.joe_e.Powerless {
	class Inner {
		
	}
	
    void foo () {
    	System.out.println();
    	
    	class Inner extends Object {
    		class MoreInner {
    			
    		}
    	}
    	
    	class OtherInner extends Inner {
    		class OtherMoreInner extends MoreInner {
    			
    		}
    	}
    }
    

}
