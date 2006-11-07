package test;

import org.joe_e.*;

public class ArrayTest {
    public static void test() {
        ConstArray<String> ca = new ConstArray<String>("foo", "bar", "baz");
        ca = ca.with("qux").with("quux").with("bugzot");
        System.out.println("ca:" + ca);
        
        ImmutableArray<String> ia = new ImmutableArray<String>("foo", "bar", "baz");
        ia = ia.with("qux").with("quux").with("bugzot");
        System.out.println("ia:" + ia);
        
        PowerlessArray<String> pa = new PowerlessArray<String>("foo", "bar", "baz");
        pa = pa.with("qux").with("quux").with("bugzot");
        System.out.println("pa:" + pa);
        
        // DataArray<String> da = new DataArray<String>("foo", "bar", "baz");
        // da = da.with("qux").with("quux").with("bugzot");
        // System.out.println("da:" + da);
    }    
}
