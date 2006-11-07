package test;

public class InnerTest {
	class Inner {
		
	}
	
    void foo () {
    	System.out.println();
    	
    	class Inner extends Object {
    		class MoreInner extends Thread {
    			
    		}
    	}
    	
    	class OtherInner extends Inner {
    		class OtherMoreInner extends MoreInner {
    			
    		}
    	}
    }
    
    void bar () {
    	class Inner extends InnerTest.Inner {
    	}
    }
}
