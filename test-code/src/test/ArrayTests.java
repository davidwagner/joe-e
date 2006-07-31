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
        
        RecordArray<Integer> ra = new RecordArray<Integer>(1, 2, 3, -6000);
        DataArray<Integer> da = new DataArray<Integer>(1, 2, 3, -6000);
        IntArray ia = new IntArray(1, 2, 3, -6000);
        RecordArray<Integer> ra2 = new RecordArray<Integer>(1, 2, 3, 6000);
        DataArray<Integer> da2 = new DataArray<Integer>(1, 2, 3, 6000);
        IntArray ia2 = new IntArray(1, 2, 3, 6000);
        
        System.out.println("RecordArray hashCode: " + ra.hashCode());
        System.out.println("DataArray hashCode: " + da.hashCode());
        System.out.println("IntArray hashCode: " + ia.hashCode());
        System.out.println("RecordArray2 hashCode: " + ra2.hashCode());
        System.out.println("DataArray2 hashCode: " + da2.hashCode());
        System.out.println("IntArray2 hashCode: " + ia2.hashCode());
     
        System.out.println("RA=RA? " + ra.equals(ra));
        System.out.println("RA=DA? " + ra.equals(da));
        System.out.println("RA=IA? " + ra.equals(ia));
        System.out.println("RA=RA2? " + ra.equals(ra2));
        System.out.println("RA=DA2? " + ra.equals(da2));
        System.out.println("RA=IA2? " + ra.equals(ia2));
        System.out.println("DA=RA? " + da.equals(ra));
        System.out.println("DA=DA? " + da.equals(da));
        System.out.println("DA=IA? " + da.equals(ia));
        System.out.println("DA=RA2? " + da.equals(ra2));
        System.out.println("DA=DA2? " + da.equals(da2));
        System.out.println("DA=IA2? " + da.equals(ia2));
        System.out.println("IA=RA? " + ia.equals(ra));
        System.out.println("IA=DA? " + ia.equals(da));
        System.out.println("IA=IA? " + ia.equals(ia));
        System.out.println("IA=RA2? " + ia.equals(ra2));
        System.out.println("IA=DA2? " + ia.equals(da2));
        System.out.println("IA=IA2? " + ia.equals(ia2));
        System.out.println("RA2=RA? " + ra2.equals(ra));
        System.out.println("RA2=DA? " + ra2.equals(da));
        System.out.println("RA2=IA? " + ra2.equals(ia));
        System.out.println("RA2=RA2? " + ra2.equals(ra2));
        System.out.println("RA2=DA2? " + ra2.equals(da2));
        System.out.println("RA2=IA2? " + ra2.equals(ia2));
        System.out.println("DA2=RA? " + da2.equals(ra));
        System.out.println("DA2=DA? " + da2.equals(da));
        System.out.println("DA2=IA? " + da2.equals(ia));
        System.out.println("DA2=RA2? " + da2.equals(ra2));
        System.out.println("DA2=DA2? " + da2.equals(da2));
        System.out.println("DA2=IA2? " + da2.equals(ia2));
        System.out.println("IA2=RA? " + ia2.equals(ra));
        System.out.println("IA2=DA? " + ia2.equals(da));
        System.out.println("IA2=IA? " + ia2.equals(ia));
        System.out.println("IA2=RA2? " + ia2.equals(ra2));
        System.out.println("IA2=DA2? " + ia2.equals(da2));
        System.out.println("IA2=IA2? " + ia2.equals(ia2));
        
    }
}
