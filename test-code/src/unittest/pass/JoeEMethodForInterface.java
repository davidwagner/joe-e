package unittest.pass;

interface HasTTaker2<T> {
	void takesT(T o);
}

// implements generic interface generically
public class JoeEMethodForInterface<T> implements HasTTaker2<T> {
	public void takesT(T t) {
		
	}	
}
