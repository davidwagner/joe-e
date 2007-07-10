package unittest.fail;

public class MethodFromDisabledClass3 {
	void foo2() {
		FieldFromDisabledClass2 ffdc2 = new FieldFromDisabledClass2();
		ffdc2.foo();
	}
}
