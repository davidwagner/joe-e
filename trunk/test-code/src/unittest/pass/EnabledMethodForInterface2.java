package unittest.pass;

interface Equalsable<T> {
	boolean equals(T o);
}

// implements generic interface non-generically
public class EnabledMethodForInterface2 implements Equalsable {
	
}
