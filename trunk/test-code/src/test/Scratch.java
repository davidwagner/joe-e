package test;

//import org.joe_e.*;
import java.util.Arrays;



public class Scratch {
    final transient int WTF = 6;
    
    enum Pie {
        PECAN, KEY_LIME;
        
        static boolean fresh = true;
        boolean isFresh() {
            return fresh;
        }
       
        void age() {
            fresh = false;
        }
    }
    
    static void printdir(java.io.File f) {
        System.out.print("contents of dir \"" + f.getPath() + "\": ");
        System.out.println(Arrays.toString(f.list()));
    }
    
    static class MyThrowable extends Throwable {
        final static int serialVersionUID = 1;
        
    }

    public static void foob() throws MyThrowable {
        throw new MyThrowable();
    }
    
    
    public static void main(String args[]) {

        
        // ArrayTest.test();
        // ConstArray<Character> sa = new CharArray('a', 'b', 'c');
        int[] arr = {1, 2, 3, 4, 5, 6};
        System.out.println(arr);

        String s = "" + arr;
        
        System.out.println(5 + "foo");
        System.out.println(4 + 5 + "foo" + 4 + 5);
        
        java.io.File empty = new java.io.File("");
        java.io.File cur = new java.io.File(".");
        java.io.File childOfEmpty = new java.io.File(empty, "src");
        java.io.File childOfCur = new java.io.File(cur, "src");
        java.io.File src = new java.io.File("src");
        
        System.out.println(empty.getAbsolutePath());
        System.out.println(cur.getAbsolutePath());
        System.out.println(childOfEmpty.getAbsolutePath());
        System.out.println(childOfCur.getAbsolutePath());
        System.out.println(src.getAbsolutePath());
                
        printdir(empty);
        printdir(cur);
        
        
       /*
        for (char c : sa) {
            System.out.println(c);
        }
        */
        
        //return 0;
        
        // assert (false);
    }
    
    static void parsimony() {
        final int[] poop = new int[]{2, 3, 4};
        
        class Accomplice {
            void rewriteHistory(int newPast) {
                poop[0] = newPast;
            }
            
            int moveAlong() {
                return poop[0];
            }
        }

        class Culprit implements org.joe_e.Immutable {
            void IdintdoNoffink(int noffink) {
                new Accomplice().rewriteHistory(noffink);
            }
        }
    }
}
