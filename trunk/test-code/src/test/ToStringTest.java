package test;

public class ToStringTest {
	
	public static void test () {
		System.out.println("a" + 5);
		System.out.println(5 + 3 + "a" + 2 + 5);
		System.out.println("" + new Object());
		System.out.println("" + new int[] {1, 2, 3} + 5 + new Bad() +
						   new StrawMan("p") + 2.5 + "tau");
		System.out.println("" + new ToStringTest());
	}
	
	public String toString() {
		return super.toString();
	}
}
