package unittest.fail;

public class ReferencesDisabledClass4b {
	public String toString() {
		new java.util.Hashtable() {
			static final long serialVersionUID = 1;
		};
		return "foo";
	}
}
