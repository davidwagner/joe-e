package test;

import org.joe_e.array.ConstArray;

/*
 * Test for JLS compliant implementation of enhanced for-loops.
 * According to the JLS3 14.14.2, they are given equivalent source code, which
 * means that heap corruption is detected when the .iterator() method is called
 * on an object of an unexpected type.  
 * 
 * This is in contrast to string conversion of an expression e, which is *not* 
 * equivalent to e.toString() when e is of a pointer type.  
 * See EvilStringConversion.java
 */
public class EvilIterable {
    boolean done = false;
    
    ConstArray<String[]> casa =
        ConstArray.array(new String[]{"hello", "good-bye"}, 
                         new String[]{"hola", "adios"});
    
    // this warning is kind of dumb.
    ConstArray<ConstArray<String>> cacas =   
        ConstArray.array(ConstArray.array("hello", "good-bye"),
                         ConstArray.array("hello", "good-bye"));
   
    class FakerIterator implements java.util.Iterator {       
        public String next() {
            return "pure evil";
        }
        
        public boolean hasNext() {
            if (done) {
                return false;
            } else {
                done = false;
                return true;
            }
        }
        
        public void remove() {
            throw new UnsupportedOperationException(); 
        }
    }
    
    class Faker implements Iterable {
        public java.util.Iterator iterator() {
            return new FakerIterator();
        }
    }
    
    public void foo() {
        System.out.println("Enhanced for-loop Iterable tests");
        
        for (String s : casa.get(0)) {
            System.out.println(s);
        }
        
        System.out.println("ConstArray<String[]> casa");
        System.out.println("casa is replaced with an impostor");
        // this warning is not :)
        casa = (ConstArray<String[]>) (ConstArray) ConstArray.array(new Faker());

        try {
            for (String s : casa.get(0)) {
                System.out.println(s);
            }
            System.out.println("and it's fooled! uh-oh.");    
        } catch (ClassCastException cce) {
            System.out.println("but it's not fooled: " + cce);
        }    
        
        for (String s : cacas.get(0)) {
            System.out.println(s);
        }
                
        System.out.println("ConstArray<ConstArray<String>> cacas");
        System.out.println("cacas is replaced with an impostor");
        cacas = (ConstArray<ConstArray<String>>) (ConstArray) ConstArray.array(new Faker());

        try {
            for (String s : cacas.get(0)) {
                System.out.println(s);
            }
            System.out.println("and it's fooled! uh-oh.");        
        } catch (ClassCastException cce) {
            System.out.println("but it's not fooled: " + cce);
        }
    }
}
