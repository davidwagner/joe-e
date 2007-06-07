package test.library.array;

import org.joe_e.array.*;
import java.util.Arrays;

public class EqualsAndHashCode {

    public static void test() {
        Object[] array1 = new Object[]{1, 2, 3, -6000};
        Integer[] array2 = new Integer[]{1, 2, 3, -6000};
        int[] array3 = new int[]{1, 2, 3, -6000};
        
        //System.out.println("Object array hashCode: " + Arrays.hashCode(array1));
        //System.out.println("Integer array hashCode: " + Arrays.hashCode(array2));
        //System.out.println("int array hashCode: " + Arrays.hashCode(array3));
        assert(Arrays.hashCode(array1) == Arrays.hashCode(array2));
        assert(Arrays.hashCode(array1) == Arrays.hashCode(array3));
        
        ConstArray<Integer> cai = ConstArray.array(1, 2, 3, -6000);
        PowerlessArray<Integer> pai = PowerlessArray.array(1, 2, 3, -6000);
        IntArray ia = IntArray.array(1, 2, 3, -6000);
        ConstArray<Integer> cai2 = ConstArray.array(1, 2, 3, 6000);
        PowerlessArray<Integer> pai2 = PowerlessArray.array(1, 2, 3, 6000);
        IntArray ia2 = IntArray.array(1, 2, 3, 6000);
        
        assert(cai.hashCode() == Arrays.hashCode(array1));
        assert(pai.hashCode() == cai.hashCode());
        assert(ia.hashCode() == cai.hashCode());
        assert(cai2.hashCode() == pai2.hashCode());
        assert(cai2.hashCode() == ia2.hashCode());
        
        //System.out.println("ConstArray hashCode: " + ca.hashCode());
        //System.out.println("PowerlessArray hashCode: " + pa.hashCode());
        //System.out.println("IntArray hashCode: " + ia.hashCode());
        //System.out.println("ConstArray2 hashCode: " + ca2.hashCode());
        //System.out.println("PowerlessArray2 hashCode: " + pa2.hashCode());
        //System.out.println("IntArray2 hashCode: " + ia2.hashCode());
     
        assert(cai.equals(cai));
        assert(cai.equals(pai));
        assert(cai.equals(ia));
        assert(!cai.equals(cai2));
        assert(!cai.equals(pai2));
        assert(!cai.equals(ia2));
        assert(pai.equals(cai));
        assert(pai.equals(pai));
        assert(pai.equals(ia));
        assert(!pai.equals(cai2));
        assert(!pai.equals(pai2));
        assert(!pai.equals(ia2));
        assert(ia.equals(cai));
        assert(ia.equals(pai));
        assert(ia.equals(ia));
        assert(!ia.equals(cai2));
        assert(!ia.equals(pai2));
        assert(!ia.equals(ia2));
        assert(!cai2.equals(cai));
        assert(!cai2.equals(pai));
        assert(!cai2.equals(ia));
        assert(cai2.equals(cai2));
        assert(cai2.equals(pai2));
        assert(cai2.equals(ia2));
        assert(!pai2.equals(cai));
        assert(!pai2.equals(pai));
        assert(!pai2.equals(ia));
        assert(pai2.equals(cai2));
        assert(pai2.equals(pai2));
        assert(pai2.equals(ia2));
        assert(!ia2.equals(cai));
        assert(!ia2.equals(pai));
        assert(!ia2.equals(ia));
        assert(ia2.equals(cai2));
        assert(ia2.equals(pai2));
        assert(ia2.equals(ia2));
        
        /*
        System.out.println("CA=CA? " + ca.equals(ca));
        System.out.println("CA=PA? " + ca.equals(pa));
        System.out.println("CA=IA? " + ca.equals(ia));
        System.out.println("CA=CA2? " + ca.equals(ca2));
        System.out.println("CA=PA2? " + ca.equals(pa2));
        System.out.println("CA=IA2? " + ca.equals(ia2));
        System.out.println("PA=CA? " + pa.equals(ca));
        System.out.println("PA=PA? " + pa.equals(pa));
        System.out.println("PA=IA? " + pa.equals(ia));
        System.out.println("PA=CA2? " + pa.equals(ca2));
        System.out.println("PA=PA2? " + pa.equals(pa2));
        System.out.println("PA=IA2? " + pa.equals(ia2));
        System.out.println("IA=CA? " + ia.equals(ca));
        System.out.println("IA=PA? " + ia.equals(pa));
        System.out.println("IA=IA? " + ia.equals(ia));
        System.out.println("IA=CA2? " + ia.equals(ca2));
        System.out.println("IA=PA2? " + ia.equals(pa2));
        System.out.println("IA=IA2? " + ia.equals(ia2));
        System.out.println("CA2=CA? " + ca2.equals(ca));
        System.out.println("CA2=PA? " + ca2.equals(pa));
        System.out.println("CA2=IA? " + ca2.equals(ia));
        System.out.println("CA2=CA2? " + ca2.equals(ca2));
        System.out.println("CA2=PA2? " + ca2.equals(pa2));
        System.out.println("CA2=IA2? " + ca2.equals(ia2));
        System.out.println("PA2=CA? " + pa2.equals(ca));
        System.out.println("PA2=PA? " + pa2.equals(pa));
        System.out.println("PA2=IA? " + pa2.equals(ia));
        System.out.println("PA2=CA2? " + pa2.equals(ca2));
        System.out.println("PA2=PA2? " + pa2.equals(pa2));
        System.out.println("PA2=IA2? " + pa2.equals(ia2));
        System.out.println("IA2=CA? " + ia2.equals(ca));
        System.out.println("IA2=PA? " + ia2.equals(pa));
        System.out.println("IA2=IA? " + ia2.equals(ia));
        System.out.println("IA2=CA2? " + ia2.equals(ca2));
        System.out.println("IA2=PA2? " + ia2.equals(pa2));
        System.out.println("IA2=IA2? " + ia2.equals(ia2));
        */
        
        Object[] ao = {"hello", "good-bye", 2, null, 3, 3.14};
        ConstArray<Object> cao = ConstArray.array((Object)"hello", "good-bye", 2, null, 3, 3.14);
        ConstArray<Object> cao2 = ConstArray.array((Object)"hello", "good-bye", 2, null, 3, 3.14);
        assert(cao.equals(cao2));
        assert(cao2.equals(cao));
        assert(cao.hashCode() == cao2.hashCode());
        assert(cao.hashCode() == Arrays.hashCode(ao));
    }
}
