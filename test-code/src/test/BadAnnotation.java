package test;

public @interface BadAnnotation {
	static int notAProblem = 3;
	final static int[] evilArray = {1, 2, 3};  // ouch, this conveys authority!
}
