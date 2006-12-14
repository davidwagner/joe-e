package test;

import org.joe_e.Powerless;

public class Bad extends Thread {
    static int foo; // field non-final
	static int[] foofoo; // non-final field of capable type

	static final int bar = 43; // OK
	final static int baz = 39; // OK
	
	static String weebl;  // field non-final
	
	static final int[] arr = new int[] {1, 2, 3}; // type capable
    int crashy = arr.length;
	
	static final Bad badd = new Bad(); // type capable, construction of Bad OK
	
    final int g;
    
	int f; // OK
	String str; // OK
	int[] foof; // OK
	
	Bad() {
		System.out.println(this);
	
		this.f = foo; // OK
		
		Object o = new Object();
		Thread t = new Thread();
		o.getClass();
		
		g = 3;
	}
    
    Bad(int ignored) {
       Powerless p = new Powerless () {

           int foo() {
               return g;
           }
       };
       
       // Leak p somewhere
       
       g = 5;
    }
}
