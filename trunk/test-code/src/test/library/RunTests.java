package test.library;

import org.joe_e.JoeE;

public class RunTests {
    public static void main(String[] args) {
        test();
    }
    
    public static void test() {
        boolean assertsOn = false;
        assert assertsOn = true;
        if (!assertsOn) {
    	    System.out.println("Assertions disabled!  These tests require " +
                               "running with them enabled (java -ea)");
    	    JoeE.abort(new Error());
        }
       
        try {
            test.library.array.RunTests.test();
            test.library.charset.RunTests.test();
            test.library.file.RunTests.test();
            test.library.reflect.RunTests.test();
        }
        
        catch (AssertionError ae) {
            System.out.println("A test failed.");
            ae.printStackTrace();
            JoeE.abort(ae);
        } catch (Throwable t) {
            System.out.println("Something else failed.");
            t.printStackTrace();
            JoeE.abort(new Error(t));          
        }
        
        System.out.println("All tests passed.");
    }
}