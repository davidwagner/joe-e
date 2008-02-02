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
    	   System.out.println("Assertions disabled! Please run with them enabled.");
    	   JoeE.abort(new Error());
       }
       
       try {
            test.library.array.RunTests.test();
            test.library.charset.RunTests.test();
            test.library.file.RunTests.test();
            test.library.reflect.RunTests.test();
        }
        
        catch (Error e) {
            System.out.println("Something failed.");
            e.printStackTrace();
            JoeE.abort(e);
        }
        
        System.out.println("All tests passed.");
    }
}