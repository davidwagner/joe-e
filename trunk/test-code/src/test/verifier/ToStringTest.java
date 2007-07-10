package test.verifier;

import test.StrawMan;

public class ToStringTest {
	
	public static void test () {
		String s = "foo";
	    //s = 5;
	    s = "" + 5;
	    s = 5 + "";
	    s = 5 + "" + 5;
	    s = 5 + 5 + "";
	    //s = new Object();
	    s = "" + new Object();
	    s = new Object() + "";
	    s = "" + new Object() + new Object();
	    s = new Object() + "" + new Object();
	    //s = new Object() + new Object() + "";
	    //s = new Integer(5);
	    s = "" + new Integer(5);
	    s = new Integer(5) + "";
	    s = "" + new Integer(5) + new Integer(5);
	    s = new Integer(5) + "" + new Integer(5);
	    s = new Integer(5) + new Integer(5) + "";
	    
	    s = "a" + 5;
		s += "a" + 5;
		s = 5 + 3 + "a" + 2 + 5;
		s += 5 + 3 + "a" + 2 + 5;
		s += "" + new Object();
		s += new Object();
		s += new Object() + "";
		s += "" + new Bad() + new int[] {1, 2, 3} + 5 + new Bad() +
						   new StrawMan("p") + 2.5 + "tau";
		s += new Bad();
		System.out.println(s);
		// String i = ((Object) new Integer(4)) + "q";
	}
	
	
	//public String toString() {
	//	
	//	//return super.toString();
	//}
}
