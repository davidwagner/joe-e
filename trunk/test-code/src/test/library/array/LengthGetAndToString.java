package test.library.array;

import org.joe_e.array.*;

import java.util.Arrays;

public class LengthGetAndToString {
    public static void test() {
        // Make sure clones are made of passed in arrays.
        int[] intArr = {1, 2, 3};
        char[] charArr = {'a', 'b', 'c'};
        String[] strArr = {"a", "b", "c"};
        
        IntArray ia = IntArray.array(intArr);
        intArr[0] = -5;
        assert ia.get(0) == 1;
        
        CharArray ca = CharArray.array(charArr);
        charArr[0] = 'q';
        assert ca.get(0) == 'a';
        
        ConstArray<String> cas = ConstArray.array(strArr);
        ImmutableArray<String> ias = ImmutableArray.array(strArr);
        PowerlessArray<String> pas = PowerlessArray.array(strArr);
        strArr[0] = "notA";
        assert cas.get(0).equals("a");
        assert ias.get(0).equals("a");
        assert pas.get(0).equals("a");
           
        String[] strings = {"foo", "bar", "baz", "qux", "quux", "bugzot"};
        
        ConstArray<String> cas2 = ConstArray.array("foo", "bar", "baz");
        cas2 = cas2.with("qux").with("quux").with("bugzot");
        //System.out.println("ca:" + ca);
        
        ImmutableArray<String> ias2 = ImmutableArray.array("foo", "bar", "baz");
        ias2 = ias2.with("qux").with("quux").with("bugzot");
        //System.out.println("ia:" + ia);
        
        PowerlessArray<String> pas2 = PowerlessArray.array("foo", "bar", "baz");
        pas2 = pas2.with("qux").with("quux").with("bugzot");
        
        byte[] bytes = {21, 45, 23, 100, -31, 23};
        
        ByteArray ba = ByteArray.array((byte) 21, (byte) 45, (byte) 23);
        ba = ba.with((byte) 100).with((byte) -31).with((byte) 23);
        
        ConstArray<Object> cao = ConstArray.array(new Object());
        
        //System.out.println("pa:" + pa);
        assert cas2.length() == 6;
        assert ias2.length() == 6;
        assert pas2.length() == 6;
        assert ba.length() == 6;
        assert cao.length() == 1;
        
        assert cas2.get(0).equals("foo");
        assert cas2.get(5).equals("bugzot");
        assert ias2.get(0).equals("foo");
        assert ias2.get(5).equals("bugzot");
        assert pas2.get(0).equals("foo");
        assert pas2.get(5).equals("bugzot");
        
        assert ba.get(0).equals((byte) 21);
        assert ba.getByte(0) == (byte) 21;
        assert ba.get(5).equals((byte) 23);
        assert ba.getByte(5) == (byte) 23;
        
        assert cas2.toString().equals(Arrays.toString(strings));
        assert ias2.toString().equals(Arrays.toString(strings));
        assert pas2.toString().equals(Arrays.toString(strings));
        assert ba.toString().equals(Arrays.toString(bytes));
        assert cao.toString().equals("[<unprintable>]");
    }    
}
