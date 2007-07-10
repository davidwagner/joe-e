package test.verifier;

import org.joe_e.Powerless;

public class InnerClasses {
    class One {
    	int looksHarmless() {
    		return 2;
    	}
    	
    	class Two {
    		// int q;
    		int diddle() {
    			return looksHarmless();
    		}
    	}
    }
    
    class One2 extends One {
    	int evil[] = {1, 2, 3, 4};
    	int looksHarmless() {
    		return evil[0]++;
    	}
    }
    
    static void bar () {
    	class Inner extends One.Two implements org.joe_e.Powerless {
    		Inner() {
    			new InnerClasses().new One2().super();
    		}
    	}
    }	
}
