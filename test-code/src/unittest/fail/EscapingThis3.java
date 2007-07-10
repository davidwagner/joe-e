package unittest.fail;

public class EscapingThis3 {
	Object o;
	
	class Inner {
		Inner() {
			this(5);
		}
	
		Inner(int num) {
			if (num > 10) {
				EscapingThis3.this.store(this);
			}
		}
	}
	
	void store(Object o) {
		this.o = o;
	}
}
