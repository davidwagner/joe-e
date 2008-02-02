package test;

import org.joe_e.array.ConstArray;

/*
 * Test how string conversion works when the object being converted pollutes
 * the heap. 
 * 
 * Unlike the enhanced for-loop, string conversion of an expression is not
 * defined in terms of an equivalent explicit expression.  Instead it is
 * vaguely defined to be performed "as if by an invocation of the toString
 * method of the referenced object".  Experimentally, it is *not* equivalent
 * to e.toString() when e is of a pointer type.  It appears to be the case
 * that Object's toString() method is used for method invocation in the binary
 * regardless of the inferred type of the expression, making the nearest source
 * equivalent ((Object) e).toString()
 */
public class EvilStringConversion {
    boolean done = false;

    ConstArray<String> cas =   
        ConstArray.array("hello", "good-bye");  

    ConstArray<Integer> cai =
        ConstArray.array(0, 1);
    
    ConstArray<Object> cao = 
        ConstArray.array(new Object());
    
    public static void main(String[] args) {
        new EvilStringConversion().test();
    }
    
    public void test() {
        System.out.println("String Conversion tests");
        System.out.println("ConstArray<String> cas");
        System.out.println("a string " + cas.get(0));
        System.out.println(5 + cas.get(0));
        try {
            assert false : cas.get(0);
        } catch (AssertionError ae) {
            System.out.println("used in an assertion: " + ae);
        }
                
        System.out.println("cas is replaced with a numeric impostor");
        // this warning is not :)
        cas = (ConstArray<String>) (ConstArray) cai;
        
        try {
            System.out.println("a string" + cas.get(0));
            System.out.println("and it's fooled when concatenated with a string!");    
        } catch (ClassCastException cce) {
            System.out.println("it's NOT fooled when concatenated with a string: " + cce);
        }    
      
        try {
            System.out.println(5 + cas.get(0));
            System.out.println("and it's fooled when concatenated with an int!");    
        } catch (ClassCastException cce) {
            System.out.println("it's NOT fooled when concatenated with an int: " + cce);
        }
        
        try {
            assert false : cas.get(0);
            System.out.println("and nothing is thrown when used in an assertion, wtf?");
        } catch (ClassCastException cce) {
            System.out.println("and it's NOT fooled when used in an assertion: " + cce);
        } catch (AssertionError ae) {
            System.out.println("and it's fooled when used in an assertion: " + ae);
        }
              
        System.out.println("ConstArray<Integer> cai");

        System.out.println("a string " + cai.get(0));
        try {
            assert false : cas.get(0);
        } catch (AssertionError ae) {
            System.out.println("used in an assertion: " + ae);
        }
                
        System.out.println("cai is replaced with an impostor");
        cai = (ConstArray<Integer>) (ConstArray) cao;

        try {
            System.out.println("a string" + cai.get(0));
            System.out.println("and it's fooled when concatenated with a string!");    
        } catch (ClassCastException cce) {
            System.out.println("but it's not fooled: " + cce);
        }    
        
        try {
            assert false : cai.get(0);
            System.out.println("and nothing is thrown when used in an assertion, wtf?");
        } catch (ClassCastException cce) {
            System.out.println("and it's NOT fooled when used in an assertion: " + cce);
        } catch (AssertionError ae) {
            System.out.println("and it's fooled when used in an assertion: " + ae);
        }
    }
}
