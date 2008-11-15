package unittest.fail;

class Holder {
	<T> Holder() {
		
	}
}

public class ReferencesDisabledClass3c {
	Holder h = new <org.joe_e.testlib.DisabledEnum>Holder();
}
