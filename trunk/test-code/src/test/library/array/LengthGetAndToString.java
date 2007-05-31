package test.library.array;

import org.joe_e.array.*;
import java.util.Arrays;

public class LengthGetAndToString {
    public static void test() {
        String[] strings = {"foo", "bar", "baz", "qux", "quux", "bugzot"};
        
        ConstArray<String> ca = ConstArray.array("foo", "bar", "baz");
        ca = ca.with("qux").with("quux").with("bugzot");
        //System.out.println("ca:" + ca);
        
        ImmutableArray<String> ia = ImmutableArray.array("foo", "bar", "baz");
        ia = ia.with("qux").with("quux").with("bugzot");
        //System.out.println("ia:" + ia);
        
        PowerlessArray<String> pa = PowerlessArray.array("foo", "bar", "baz");
        pa = pa.with("qux").with("quux").with("bugzot");
        
        byte[] bytes = {21, 45, 23, 100, -31, 23};
        
        ByteArray ba = ByteArray.array((byte) 21, (byte) 45, (byte) 23);
        ba = ba.with((byte) 100).with((byte) -31).with((byte) 23);
        
        ConstArray<Object> cao = ConstArray.array(new Object());
        
        //System.out.println("pa:" + pa);
        assert ca.length() == 6;
        assert ia.length() == 6;
        assert pa.length() == 6;
        assert ba.length() == 6;
        assert cao.length() == 1;
        
        assert ca.get(0).equals("foo");
        assert ca.get(5).equals("bugzot");
        assert ia.get(0).equals("foo");
        assert ia.get(5).equals("bugzot");
        assert pa.get(0).equals("foo");
        assert pa.get(5).equals("bugzot");
        
        assert ba.get(0).equals((byte) 21);
        assert ba.getByte(0) == (byte) 21;
        assert ba.get(5).equals((byte) 23);
        assert ba.getByte(5) == (byte) 23;
        
        assert ca.toString().equals(Arrays.toString(strings));
        assert ia.toString().equals(Arrays.toString(strings));
        assert pa.toString().equals(Arrays.toString(strings));
        assert ba.toString().equals(Arrays.toString(bytes));
        assert cao.toString().equals("[<unprintable>]");
    }    
}
