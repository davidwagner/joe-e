package test;

public class Bad {
	static int foo; // field non-final
	static int[] foofoo; // non-final field of capable type

	static final int bar = 43; // OK
	final static int baz = 39; // OK
	
	static String weebl;  // field non-final
	
	static final int[] arr = new int[] {1, 2, 3}; // type capable
	static final Bad badd = new Bad(); // type capable
	
	int f; // OK
	String str; // OK
	int[] foof; // OK
}
