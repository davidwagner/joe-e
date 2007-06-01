package test;
/*
 * This file should not compile; Sun's compiler and GCJ both refuse to compile
 * it.  Eclipse's compiler (ECJ) compiles it without complaint, generating
 * bytecode that violates the semantics of final fields.
 * 
 * The JVM does not check for this kind of thing.
 *
 * I've filed an Eclipse bug on this.
 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=190391
 */
public class ShouldNotCompile {
	final int contents;
	
	ShouldNotCompile() {
		contents = 3;
	}
	ShouldNotCompile(ShouldNotCompile other) {
		other.contents = 5;
	}
	
	public static void main(String[] args) {
		ShouldNotCompile one = new ShouldNotCompile();
		System.out.println("one.contents: " + one.contents);
		ShouldNotCompile two = new ShouldNotCompile(one);
		System.out.println("one.contents: " + one.contents);
		System.out.println("two.contents: " + two.contents);
	}
}
