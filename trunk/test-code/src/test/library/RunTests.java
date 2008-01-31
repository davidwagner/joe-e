package test.library;

import org.joe_e.JoeE;

public class RunTests {
    public static void main(String[] args) {
        test();
    }
    
    public static void test() {
        try {
        	assert false;
        	System.out.println("Assertions disabled! Please run with them enabled.");
        	JoeE.abort(new Error());
        } catch (AssertionError ae) {           

        }
    	
    	try {
            test.library.array.RunTests.test();
            test.library.charset.RunTests.test();
            test.library.file.RunTests.test();
            test.library.reflect.RunTests.test();
            assert false; // uncomment to verify that assertions are enabled
        }
        
        catch (Error e) {
            System.out.println("Something failed.");
            e.printStackTrace();
            JoeE.abort(e);
        }
        
        System.out.println("All tests passed.");
    }
}