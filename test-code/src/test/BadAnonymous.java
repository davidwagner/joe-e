package test;

import org.joe_e.Powerless;

public class BadAnonymous {
	static int foo; // error: not final
	
	final int g;
	
    BadAnonymous() {
       Leaker l = new Leaker();	// should be error
       l.new StillLeaker();         // also an error
       NotLeaker nl = new NotLeaker(); // OK: static
       nl.new StillNotLeaker();        // OK
       
       Powerless p = new Powerless () {   	   
    	   
           public String toString() {
               return "" + g;
           }
           
           native void poop(); // should be error: native
       }; // should be error
       
       // Leak p somewhere
       
       g = 5;
    }
    
    void badFunction() {
    	Powerless q = new Powerless() {
    		int notImmutable; // should be error (violates Powerless)
    		  		
    		final int beep;
    		final Powerless zeep = 
    			new Powerless () {
    				public String toString() {
    					return "" + beep;
    				}
    			}; // should be error
    		    		
    		{
    			// leak zeep
    			
    			evilLocal(); // should be banned.
    			
    			Object qux = new EvilLocalType(); // should be error
    			
    			// leak qux
    			
    			beep = 5;
    		}
    		
    		void evilLocal() {
    			// could leak 'this'
    		}
    		
    		class EvilLocalType {
    			EvilLocalType() {
    			}
    			
    			public String toString() {
					return "" + beep;
    			}	
    		}
    		
    	};
    }
    
    
    class Leaker {
    	public int foo() {
    		return g;
    	}
    
    	class StillLeaker {
    		
    	}
    }
    
    static class NotLeaker {
    	public int foo() {
    		// can't see g
    		return 6;
    	}
    	
    	class StillNotLeaker {
    		
    	}
    }
}
