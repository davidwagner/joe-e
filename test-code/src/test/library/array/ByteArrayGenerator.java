package test.library.array;

import org.joe_e.array.ByteArray;
import java.util.Arrays;

public class ByteArrayGenerator {
    public static void test() {
       ByteArray.Generator bagn = new ByteArray.Generator(-23);
       ByteArray.Generator bag0 = new ByteArray.Generator(0);
       ByteArray.Generator bag1 = new ByteArray.Generator(1);
       ByteArray.Generator bag1000 = new ByteArray.Generator(1000);
       
       int[] ints1 = {1, 2, 3, 4, 5, -12, 34, 100, 1000, 10000, -1000, -10000};
       byte[] bytes = {1, 2, 3, 4, 5, -12, 34, 127};
       int[][] ranges = {{0, 8}, {1, 0}, {3, 5}, {7, 1}};

       for (int i : ints1) {
           bagn.write(i);
           bag0.write(i);
           bag1.write(i);
           bag1000.write(i);
       }
       
       byte[] correct1 = {1, 2, 3, 4, 5, -12, 34, 100, (byte) 1000,
                          (byte) 10000, (byte) -1000, (byte) -10000}; 
       assert Arrays.equals(bagn.snapshot().toByteArray(), correct1);
       assert Arrays.equals(bag0.snapshot().toByteArray(), correct1);
       assert Arrays.equals(bag1.snapshot().toByteArray(), correct1);
       assert Arrays.equals(bag1000.snapshot().toByteArray(), correct1);
              
       bagn.write(bytes);
       bag0.write(bytes);
       bag1.write(bytes);
       bag1000.write(bytes);
       
       byte[] correct2 = {1, 2, 3, 4, 5, -12, 34, 100, (byte) 1000,
                          (byte) 10000, (byte) -1000, (byte) -10000,
                          1, 2, 3, 4, 5, -12, 34, 127}; 
       assert Arrays.equals(bagn.snapshot().toByteArray(), correct2);
       assert Arrays.equals(bag0.snapshot().toByteArray(), correct2);
       assert Arrays.equals(bag1.snapshot().toByteArray(), correct2);
       assert Arrays.equals(bag1000.snapshot().toByteArray(), correct2);
       
       for (int[] r : ranges) {           
           bagn.write(bytes, r[0], r[1]);
           bag0.write(bytes, r[0], r[1]);
           bag1.write(bytes, r[0], r[1]);
           bag1000.write(bytes, r[0], r[1]);
       }

       byte[] correct3 = {1, 2, 3, 4, 5, -12, 34, 100, (byte) 1000,
               (byte) 10000, (byte) -1000, (byte) -10000,
               1, 2, 3, 4, 5, -12, 34, 127,
               1, 2, 3, 4, 5, -12, 34, 127,
               4, 5, -12, 34, 127, 127}; 

       assert Arrays.equals(bagn.snapshot().toByteArray(), correct3);
       assert Arrays.equals(bag0.snapshot().toByteArray(), correct3);
       assert Arrays.equals(bag1.snapshot().toByteArray(), correct3);
       assert Arrays.equals(bag1000.snapshot().toByteArray(), correct3);

       // test write(byte[]) from scratch
       bagn = new ByteArray.Generator(-23);
       bag0 = new ByteArray.Generator(0);
       bag1 = new ByteArray.Generator(1);
       bag1000 = new ByteArray.Generator(1000);
       
       bagn.write(bytes);
       bag0.write(bytes);
       bag1.write(bytes);
       bag1000.write(bytes);
       
       byte[] correct4 = {1, 2, 3, 4, 5, -12, 34, 127}; 

       assert Arrays.equals(bagn.snapshot().toByteArray(), correct4);
       assert Arrays.equals(bag0.snapshot().toByteArray(), correct4);
       assert Arrays.equals(bag1.snapshot().toByteArray(), correct4);
       assert Arrays.equals(bag1000.snapshot().toByteArray(), correct4);

       for (int i : ints1) {
           bagn.write(i);
           bag0.write(i);
           bag1.write(i);
           bag1000.write(i);
       }
       
       byte[] correct5 = {1, 2, 3, 4, 5, -12, 34, 127,
                          1, 2, 3, 4, 5, -12, 34, 100, (byte) 1000,
                          (byte) 10000, (byte) -1000, (byte) -10000};      
       
       assert Arrays.equals(bagn.snapshot().toByteArray(), correct5);
       assert Arrays.equals(bag0.snapshot().toByteArray(), correct5);
       assert Arrays.equals(bag1.snapshot().toByteArray(), correct5);
       assert Arrays.equals(bag1000.snapshot().toByteArray(), correct5);
                     
       // test write(byte[], int, int) from scratch
       bagn = new ByteArray.Generator(-23);
       bag0 = new ByteArray.Generator(0);
       bag1 = new ByteArray.Generator(1);
       bag1000 = new ByteArray.Generator(1000);
       
       for (int[] r : ranges) {           
           bagn.write(bytes, r[0], r[1]);
           bag0.write(bytes, r[0], r[1]);
           bag1.write(bytes, r[0], r[1]);
           bag1000.write(bytes, r[0], r[1]);
       }
       
       byte[] correct6 = {1, 2, 3, 4, 5, -12, 34, 127,
                          4, 5, -12, 34, 127, 127}; 
       
       assert Arrays.equals(bagn.snapshot().toByteArray(), correct6);
       assert Arrays.equals(bag0.snapshot().toByteArray(), correct6);
       assert Arrays.equals(bag1.snapshot().toByteArray(), correct6);
       assert Arrays.equals(bag1000.snapshot().toByteArray(), correct6);
     
       // exception when length < 0
       try {
           bagn.write(bytes, 0, -23);
           assert false;
       } catch (IllegalArgumentException iae) {}
       
       ByteArray.Generator bagNew = new ByteArray.Generator();
       ByteArray someBytes = bagn.snapshot();
       java.io.InputStream byteStream = someBytes.asInputStream();
       try {
           byte[] buffer = new byte[4];
           int bytesRead = byteStream.read(buffer);
           while (bytesRead > 0) {
               bagNew.write(buffer, 0, bytesRead);
               bytesRead = byteStream.read(buffer);
           }
       } catch (java.io.IOException ioe) {
           assert false;
       }
       
       assert (bagNew.snapshot().equals(someBytes));
    }      
}
