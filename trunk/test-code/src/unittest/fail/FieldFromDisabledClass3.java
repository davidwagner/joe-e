package unittest.fail;

public class FieldFromDisabledClass3 {
	Object foo2() {
		FieldFromDisabledClass2 ffdc2 = new FieldFromDisabledClass2();
		return ffdc2.i;
	}
}
