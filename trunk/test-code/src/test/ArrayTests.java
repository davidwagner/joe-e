package test;

import org.joe_e.*;
import java.util.Arrays;

public class ArrayTests {

    public static void main(String[] args) {
        Object[] array1 = new Object[]{1, 2, 3, -6000};
        Integer[] array2 = new Integer[]{1, 2, 3, -6000};
        int[] array3 = new int[]{1, 2, 3, -6000};
        
        System.out.println("Object array hashCode: " + Arrays.hashCode(array1));
        System.out.println("Integer array hashCode: " + Arrays.hashCode(array2));
        System.out.println("int array hashCode: " + Arrays.hashCode(array3));
        
        ConstArray<Integer> ca = new ConstArray<Integer>(1, 2, 3, -6000);
        PowerlessArray<Integer> pa = new PowerlessArray<Integer>(1, 2, 3, -6000);
        IntArray ia = new IntArray(1, 2, 3, -6000);
        ConstArray<Integer> ca2 = new ConstArray<Integer>(1, 2, 3, 6000);
        PowerlessArray<Integer> pa2 = new PowerlessArray<Integer>(1, 2, 3, 6000);
        IntArray ia2 = new IntArray(1, 2, 3, 6000);
        
        System.out.println("ConstArray hashCode: " + ca.hashCode());
        System.out.println("PowerlessArray hashCode: " + pa.hashCode());
        System.out.println("IntArray hashCode: " + ia.hashCode());
        System.out.println("ConstArray2 hashCode: " + ca2.hashCode());
        System.out.println("PowerlessArray2 hashCode: " + pa2.hashCode());
        System.out.println("IntArray2 hashCode: " + ia2.hashCode());
     
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
        
    }
}
