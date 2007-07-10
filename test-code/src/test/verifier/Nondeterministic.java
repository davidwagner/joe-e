package test.verifier;
import org.joe_e.Powerless;

public class Nondeterministic {
    static class SpecificException extends Exception implements Powerless {
        static final long serialVersionUID = 1;
        final int number;
        
        SpecificException(int me) {
            number = me;
        }
    }
    
    static void f(int[] count) {
        count[0]++;
        f(count);
    }
    
    static void xx() throws SpecificException {
        int[] max = {0};
        try {
            if (2 < 5) {
                throw new SpecificException(2);
            }            
        } catch (SpecificException y) {
            f(max);
        } catch (Error ee) {
            throw org.joe_e.JoeE.abort(ee);
        } finally {}              
        /*    if (true) {
                throw new SpecificException(max[0]);
            }
        } */  
    }
    
    static int nondet() {
        try {
            xx();
            return 0;
        } catch (SpecificException se) {
            return se.number;
        }
    }
    
    public static void main(String[] args) {
        //System.out.println("RaNdO: " + nondet());
    }
}
