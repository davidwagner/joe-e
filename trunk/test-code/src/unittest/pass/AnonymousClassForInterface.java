package unittest.pass;

public class AnonymousClassForInterface {
	void foop() {
		new java.io.Serializable() {
			public static final long serialVersionUID = 1;
		};
	}
}
