package unittest.pass;

interface HasTTaker3<T> {
	void takesT(T o);
}

class CanTakeT<T> {
	public void takesT(T t) {
		
	}	
}

// implements generic interface generically
public class JoeEMethodForInterface2<T> extends CanTakeT<T>
										implements HasTTaker3<T> {
	
}
