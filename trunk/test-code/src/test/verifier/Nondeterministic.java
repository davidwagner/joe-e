package test.verifier;
import org.joe_e.Powerless;

public class Nondeterministic {
	/* Way one: catch Error */
	int recursiveNondet() {
		try {
			return 1 + recursiveNondet();
		} catch (StackOverflowError e) {
			return 1;
		}
	}
	
	/* Way two: using finally */
    static class IntException extends Exception implements Powerless {
        static final long serialVersionUID = 1;
        final int number;
        
        IntException(int me) {
            number = me;
        }
    }

    int nondet() {
        try { 
        	freemem(); return 0;
        } catch (IntException ie) {
        	return ie.number;
        }
    }

    void freemem() throws IntException {
        int shift = 0;
        try {
            for (shift = 0; ; ++shift) {
                double a[] = new double[1 << shift];
            }
        } finally {
        	throw new IntException(shift);
        }
    }
    
    public static void main(String[] args) {
        //System.out.println("Mystery1: " + recursiveNondet());
    	//System.out.println("Mystery2: " + nondet());
    }
}
