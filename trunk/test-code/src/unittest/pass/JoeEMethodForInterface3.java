package unittest.pass;

interface HasObjectTaker2<T> {
	void takesObject(T o);
}

// implements generic interface generically
public class JoeEMethodForInterface3<T> implements HasObjectTaker2 {
	public void takesObject(Object o) {
		
	}	
}
