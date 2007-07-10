package unittest.fail;

class PowerlessType2 implements org.joe_e.Powerless {
	
}

public class PowerlessInNonPowerless2 {
	void foo() {
		new PowerlessType2() {
			
		};
	}
}
