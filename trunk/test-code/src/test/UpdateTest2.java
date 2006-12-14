package test;

public class UpdateTest2 extends UpdateTest1 {

	final int bar;
	
	UpdateTest2() {
		Object deck = new Poop();
		bar = foof;
		System.out.println(deck);
	}
	
	class Poop {
		// swab the poop deck, mateys
		Poop() {
			System.out.println(this);
		}
		
		Object swab() {
			return UpdateTest2.this;
		}
	}
}
