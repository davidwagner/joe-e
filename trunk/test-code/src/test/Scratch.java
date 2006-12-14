package test;

import org.joe_e.*;
import java.util.Arrays;

public class Scratch {
    public static void main(String args[]) {
        assert (false);
        
        // ArrayTest.test();
        // ConstArray<Character> sa = new CharArray('a', 'b', 'c');
        int[] arr = {1, 2, 3, 4, 5, 6};
        System.out.println(arr);

        String s = "" + arr;
        
        System.out.println(5 + "foo");
        System.out.println(4 + 5 + "foo" + 4 + 5);
        
        /*
        for (char c : sa) {
            System.out.println(c);
        }
        */
        
        //return 0;
    }
}
