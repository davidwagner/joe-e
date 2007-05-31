package test.verifier;

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
