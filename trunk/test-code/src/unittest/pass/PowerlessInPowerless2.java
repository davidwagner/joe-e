package unittest.pass;

class PowerlessType2 implements org.joe_e.Powerless {
	
}

public class PowerlessInPowerless2 extends PowerlessType2 {
	void foo() {
		new PowerlessType2() {
			
		};
	}
}
